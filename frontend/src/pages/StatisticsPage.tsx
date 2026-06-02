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

  const totalIssues = statistics?.totalIssues ?? 0;
  const openIssues =
    (statistics?.byStatus.NEW ?? 0) +
    (statistics?.byStatus.ASSIGNED ?? 0) +
    (statistics?.byStatus.REOPENED ?? 0);

  const resolvedIssues =
    (statistics?.byStatus.RESOLVED ?? 0) + (statistics?.byStatus.CLOSED ?? 0);

  const resolutionRate =
    totalIssues === 0 ? 0 : Math.round((resolvedIssues / totalIssues) * 100);

  return (
    <section className="page-section">
      <div className="page-header-row">
        <div>
          <h2>통계</h2>
          <p>백엔드 API에서 이슈 발생 현황과 처리 추이를 확인합니다.</p>
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
              label="해결률"
              value={resolutionRate}
              description="%"
            />
          </div>

          <IssueStatusChart items={statusStats} />
          <IssueTrendTable rows={monthlyTrendRows} />

          <article className="statistics-card">
            <h3>담당자별 이슈 수</h3>
            <ul>
              {Object.entries(statistics.byAssignee).map(([assignee, count]) => (
                <li key={assignee}>
                  {assignee}: {count}
                </li>
              ))}
            </ul>
          </article>
        </>
      )}
    </section>
  );
}

export default StatisticsPage;