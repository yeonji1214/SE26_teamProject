import { useEffect, useMemo, useState } from "react";
import { getStatistics } from "../api/statisticsApi";
import IssueStatusChart from "../components/IssueStatusChart";
import IssueTrendTable from "../components/IssueTrendTable";
import StatisticSummaryCard from "../components/StatisticSummaryCard";
import type { StatisticsResponse } from "../types/statistics";

function StatisticsPage() {
  const [statistics, setStatistics] = useState<StatisticsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    loadStatistics();
  }, []);

  const loadStatistics = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const data = await getStatistics();
      setStatistics(data);
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "통계 정보를 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const statusStats = useMemo(() => {
    if (!statistics) {
      return [];
    }

    return Object.entries(statistics.byStatus).map(([label, count]) => ({
      label,
      count,
    }));
  }, [statistics]);

  const priorityStats = useMemo(() => {
    if (!statistics) {
      return [];
    }

    return Object.entries(statistics.byPriority).map(([label, count]) => ({
      label,
      count,
    }));
  }, [statistics]);

  const dailyRows = useMemo(() => {
    if (!statistics) {
      return [];
    }

    return Object.entries(statistics.byDay).map(([period, created]) => ({
      period,
      created,
    }));
  }, [statistics]);

  const monthlyTrendRows = useMemo(() => {
    if (!statistics) {
      return [];
    }

    return Object.entries(statistics.byMonth).map(([period, created]) => ({
      period,
      created,
      resolved: 0,
      closed: 0,
    }));
  }, [statistics]);

  const assigneeRows = useMemo(() => {
    if (!statistics) {
      return [];
    }

    return Object.entries(statistics.byAssignee).map(([assignee, count]) => ({
      assignee,
      count,
    }));
  }, [statistics]);

  const totalIssues = statistics?.totalIssues ?? 0;

  const openIssues =
    (statistics?.byStatus["NEW"] ?? 0) +
    (statistics?.byStatus["ASSIGNED"] ?? 0) +
    (statistics?.byStatus["REOPENED"] ?? 0);

  const resolvedIssues =
    (statistics?.byStatus["RESOLVED"] ?? 0) +
    (statistics?.byStatus["CLOSED"] ?? 0);

  const highPriorityIssues =
    (statistics?.byPriority["BLOCKER"] ?? 0) +
    (statistics?.byPriority["CRITICAL"] ?? 0);

  const resolutionRate =
    totalIssues === 0 ? 0 : Math.round((resolvedIssues / totalIssues) * 100);

  return (
    <section className="page-section">
      <div className="page-header-row">
        <div>
          <h2>통계</h2>
          <p>백엔드 API에서 이슈 발생 현황, 우선순위, 담당자 분포를 확인합니다.</p>
        </div>
      </div>

      {isLoading && <p>통계 정보를 불러오는 중입니다.</p>}
      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {statistics && !isLoading && !errorMessage && (
        <>
          <div className="statistics-summary-grid">
            <StatisticSummaryCard
              label="전체 이슈"
              value={totalIssues}
              description="등록된 전체 이슈 수"
            />
            <StatisticSummaryCard
              label="진행 중"
              value={openIssues}
              description="NEW, ASSIGNED, REOPENED"
            />
            <StatisticSummaryCard
              label="해결/종료"
              value={resolvedIssues}
              description="RESOLVED, CLOSED"
            />
            <StatisticSummaryCard
              label="고우선순위"
              value={highPriorityIssues}
              description="BLOCKER, CRITICAL"
            />
            <StatisticSummaryCard
              label="해결률"
              value={resolutionRate}
              description="%"
            />
          </div>

          <IssueStatusChart items={statusStats} />

          <article className="statistics-card">
            <h3>우선순위별 이슈 분포</h3>
            <table className="issue-trend-table">
              <thead>
                <tr>
                  <th>우선순위</th>
                  <th>이슈 수</th>
                </tr>
              </thead>
              <tbody>
                {priorityStats.map((item) => (
                  <tr key={item.label}>
                    <td>{item.label}</td>
                    <td>{item.count}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </article>

          <IssueTrendTable rows={monthlyTrendRows} />

          <article className="statistics-card">
            <h3>일별 이슈 발생 수</h3>
            <table className="issue-trend-table">
              <thead>
                <tr>
                  <th>날짜</th>
                  <th>생성</th>
                </tr>
              </thead>
              <tbody>
                {dailyRows.map((row) => (
                  <tr key={row.period}>
                    <td>{row.period}</td>
                    <td>{row.created}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </article>

          <article className="statistics-card">
            <h3>담당자별 이슈 수</h3>
            <table className="issue-trend-table">
              <thead>
                <tr>
                  <th>담당자</th>
                  <th>이슈 수</th>
                </tr>
              </thead>
              <tbody>
                {assigneeRows.map((row) => (
                  <tr key={row.assignee}>
                    <td>{row.assignee}</td>
                    <td>{row.count}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </article>
        </>
      )}
    </section>
  );
}

export default StatisticsPage;