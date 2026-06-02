import { useEffect, useMemo, useState } from "react";
import { getProjects } from "../api/projectApi";
import { getStatisticsByProjectFromIssues } from "../api/statisticsApi";
import IssueStatusChart from "../components/IssueStatusChart";
import IssueTrendTable from "../components/IssueTrendTable";
import StatisticSummaryCard from "../components/StatisticSummaryCard";
import type { Project } from "../types/project";
import type { StatisticsResponse } from "../types/statistics";

function StatisticsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedProjectId, setSelectedProjectId] = useState<number | null>(
    null
  );
  const [statistics, setStatistics] = useState<StatisticsResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    loadProjects();
  }, []);

  useEffect(() => {
    if (selectedProjectId === null) {
      return;
    }

    loadStatistics(selectedProjectId);
  }, [selectedProjectId]);

  const loadProjects = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const data = await getProjects();
      setProjects(data);

      if (data.length > 0) {
        setSelectedProjectId(data[0].id);
      } else {
        setStatistics(null);
      }
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "프로젝트 목록을 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const loadStatistics = async (projectId: number) => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const data = await getStatisticsByProjectFromIssues(projectId);
      setStatistics(data);
    } catch (error) {
      setStatistics(null);
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "통계 정보를 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const selectedProject = useMemo(() => {
    return projects.find((project) => project.id === selectedProjectId) ?? null;
  }, [projects, selectedProjectId]);

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
      resolved: statistics.byStatus.RESOLVED ?? 0,
      closed: statistics.byStatus.CLOSED ?? 0,
    }));
  }, [statistics]);

  const totalIssues = statistics?.totalIssues ?? 0;

  const openIssues =
    (statistics?.byStatus.NEW ?? 0) +
    (statistics?.byStatus.ASSIGNED ?? 0) +
    (statistics?.byStatus.REOPENED ?? 0);

  const resolvedIssues =
    (statistics?.byStatus.RESOLVED ?? 0) +
    (statistics?.byStatus.CLOSED ?? 0);

  const resolutionRate =
    totalIssues === 0 ? 0 : Math.round((resolvedIssues / totalIssues) * 100);

  return (
    <section className="statistics-page">
      <div className="statistics-header">
        <div>
          <h2>통계</h2>
          <p>프로젝트별 이슈 발생 현황과 처리 추이를 확인합니다.</p>
        </div>

        <div className="statistics-project-selector">
          <span>프로젝트</span>
          <select
            value={selectedProjectId ?? ""}
            onChange={(event) =>
              setSelectedProjectId(Number(event.target.value))
            }
            disabled={projects.length === 0}
          >
            {projects.map((project) => (
              <option key={project.id} value={project.id}>
                {project.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {isLoading && <p>통계 정보를 불러오는 중입니다.</p>}
      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {!isLoading && !errorMessage && projects.length === 0 && (
        <p>등록된 프로젝트가 없습니다.</p>
      )}

      {statistics && !isLoading && !errorMessage && (
        <>
          <div className="selected-project-summary">
            <span className="project-chip">
              {selectedProject?.name ?? `Project ${selectedProjectId}`}
            </span>

            {selectedProject?.description && (
              <p>{selectedProject.description}</p>
            )}
          </div>

          <div className="stat-summary-grid">
            <StatisticSummaryCard
              label="전체 이슈"
              value={totalIssues}
              description="선택한 프로젝트 기준"
            />
            <StatisticSummaryCard
              label="미해결 이슈"
              value={openIssues}
              description="NEW / ASSIGNED / REOPENED"
            />
            <StatisticSummaryCard
              label="해결된 이슈"
              value={resolvedIssues}
              description="RESOLVED / CLOSED"
            />
            <StatisticSummaryCard
              label="해결률"
              value={resolutionRate}
              description="%"
            />
          </div>

          <div className="statistics-grid">
            <IssueStatusChart items={statusStats} />
            <IssueTrendTable rows={monthlyTrendRows} />
          </div>

          <article className="stat-panel assignee-stat-panel">
            <h3 className="panel-title">담당자별 이슈 수</h3>

            {Object.keys(statistics.byAssignee).length === 0 ? (
              <p className="empty-message">담당자별 통계가 없습니다.</p>
            ) : (
              <ul className="assignee-stat-list">
                {Object.entries(statistics.byAssignee).map(
                  ([assignee, count]) => (
                    <li key={assignee}>
                      <span>{assignee}</span>
                      <strong>{count}</strong>
                    </li>
                  )
                )}
              </ul>
            )}
          </article>
        </>
      )}
    </section>
  );
}

export default StatisticsPage;