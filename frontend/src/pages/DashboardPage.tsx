import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import IssueSummaryCard from "../components/IssueSummaryCard";
import RecentIssueTable, {
  type RecentIssueRow,
} from "../components/RecentIssueTable";
import type { Issue } from "../types/issue";
import type { Project } from "../types/project";
import { getProjectById } from "../api/projectApi";
import { getIssuesByProjectId } from "../api/issueApi";

function DashboardPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const numericProjectId = Number(projectId ?? 1);

  const [project, setProject] = useState<Project | null>(null);
  const [issues, setIssues] = useState<Issue[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!numericProjectId || Number.isNaN(numericProjectId)) {
      setErrorMessage("잘못된 프로젝트 접근입니다.");
      setIsLoading(false);
      return;
    }

    const loadDashboardData = async () => {
      setIsLoading(true);
      setErrorMessage("");

      try {
        const [projectData, issueData] = await Promise.all([
          getProjectById(numericProjectId),
          getIssuesByProjectId(numericProjectId),
        ]);

        setProject(projectData);
        setIssues(issueData);
      } catch (error) {
        setErrorMessage(
          error instanceof Error
            ? error.message
            : "대시보드 정보를 불러오지 못했습니다."
        );
      } finally {
        setIsLoading(false);
      }
    };

    loadDashboardData();
  }, [numericProjectId]);

  const summary = useMemo(() => {
    return {
      total: issues.length,
      new: issues.filter((issue) => issue.status === "NEW").length,
      assigned: issues.filter((issue) => issue.status === "ASSIGNED").length,
      resolved: issues.filter((issue) => issue.status === "RESOLVED").length,
      closed: issues.filter((issue) => issue.status === "CLOSED").length,
    };
  }, [issues]);

  const myTasks = useMemo(() => {
    return issues.filter((issue) => issue.assignee?.username === "dev1");
  }, [issues]);

  const recentIssues: RecentIssueRow[] = useMemo(() => {
    return issues.slice(0, 5).map((issue) => ({
      id: issue.id,
      title: issue.title,
      status: issue.status,
      priority: issue.priority,
      assignee: issue.assignee?.username ?? "-",
      lastActivity: issue.reportedDate ?? "-",
    }));
  }, [issues]);

  if (isLoading) {
    return (
      <section className="dashboard-page">
        <p>대시보드 정보를 불러오는 중입니다.</p>
      </section>
    );
  }

  if (errorMessage) {
    return (
      <section className="dashboard-page">
        <p className="error-message">{errorMessage}</p>
      </section>
    );
  }

  return (
    <section className="dashboard-page">
      <div className="dashboard-top-bar">
        <h2>대시보드</h2>

        <button type="button" className="project-chip">
          {project?.name ?? `Project ${numericProjectId}`}
        </button>
      </div>

      <div className="summary-card-row">
        <IssueSummaryCard label="전체 이슈" count={summary.total} />
        <IssueSummaryCard label="New" count={summary.new} />
        <IssueSummaryCard label="Assigned" count={summary.assigned} />
        <IssueSummaryCard label="Resolved" count={summary.resolved} />
        <IssueSummaryCard label="Closed" count={summary.closed} />
      </div>

      <div className="dashboard-main-grid">
        <article className="my-task-panel">
          <div className="panel-title">내 할 일</div>

          {myTasks.length > 0 ? (
            <ul className="my-task-list">
              {myTasks.map((issue) => (
                <li
                  key={issue.id}
                  onClick={() => navigate(`/issues/${issue.id}`)}
                >
                  <strong>#{issue.id}</strong>
                  <span>{issue.title}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="dashboard-empty-message">할당된 이슈가 없습니다.</p>
          )}
        </article>

        <RecentIssueTable
          issues={recentIssues}
          onSelectIssue={(issueId) => navigate(`/issues/${issueId}`)}
        />
      </div>
    </section>
  );
}

export default DashboardPage;