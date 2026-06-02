import { type ChangeEvent, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import IssueSummaryCard from "../components/IssueSummaryCard";
import RecentIssueTable, {
  type RecentIssueRow,
} from "../components/RecentIssueTable";
import { getIssuesByProjectId } from "../api/issueApi";
import { getProjects } from "../api/projectApi";
import type { Issue } from "../types/issue";
import type { Project } from "../types/project";
import type { User } from "../types/user";
import { AUTH_CHANGE_EVENT, getCurrentUser } from "../utils/authStorage";

function DashboardPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const numericProjectId = projectId ? Number(projectId) : null;

  const [projects, setProjects] = useState<Project[]>([]);
  const [issues, setIssues] = useState<Issue[]>([]);
  const [currentUser, setCurrentUser] = useState<User | null>(() =>
    getCurrentUser()
  );
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const syncCurrentUser = () => {
      setCurrentUser(getCurrentUser());
    };

    syncCurrentUser();

    window.addEventListener(AUTH_CHANGE_EVENT, syncCurrentUser);
    window.addEventListener("storage", syncCurrentUser);

    return () => {
      window.removeEventListener(AUTH_CHANGE_EVENT, syncCurrentUser);
      window.removeEventListener("storage", syncCurrentUser);
    };
  }, []);

  useEffect(() => {
    const loadProjects = async () => {
      setIsLoading(true);
      setErrorMessage("");

      try {
        const projectData = await getProjects();
        setProjects(projectData);

        if (!projectId && projectData.length > 0) {
          navigate(`/dashboard/${projectData[0].id}`, { replace: true });
        }

        if (projectData.length === 0) {
          setIssues([]);
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

    loadProjects();
  }, [projectId, navigate]);

  useEffect(() => {
    if (numericProjectId === null) {
      return;
    }

    if (Number.isNaN(numericProjectId)) {
      setErrorMessage("잘못된 프로젝트 접근입니다.");
      setIsLoading(false);
      return;
    }

    const loadDashboardData = async () => {
      setIsLoading(true);
      setErrorMessage("");

      try {
        const issueData = await getIssuesByProjectId(numericProjectId);
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

  const selectedProject = useMemo(() => {
    if (numericProjectId === null) {
      return null;
    }

    return projects.find((project) => project.id === numericProjectId) ?? null;
  }, [projects, numericProjectId]);

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
    if (!currentUser) {
      return [];
    }

    if (currentUser.role === "ADMIN") {
      return issues;
    }

    if (currentUser.role === "PL") {
      return issues.filter(
        (issue) => issue.status === "NEW" || issue.status === "RESOLVED"
      );
    }

    if (currentUser.role === "DEV") {
      return issues.filter(
        (issue) =>
          issue.assignee?.id === currentUser.id &&
          issue.status === "ASSIGNED"
      );
    }

    if (currentUser.role === "TESTER") {
      return issues.filter(
        (issue) =>
          issue.reporter.id === currentUser.id && issue.status === "FIXED"
      );
    }

    return [];
  }, [issues, currentUser]);

  const myTaskTitle = useMemo(() => {
    if (!currentUser) {
      return "내 할 일";
    }

    if (currentUser.role === "ADMIN") {
      return "전체 이슈";
    }

    if (currentUser.role === "PL") {
      return "검토할 이슈";
    }

    if (currentUser.role === "DEV") {
      return "내 할 일";
    }

    if (currentUser.role === "TESTER") {
      return "확인할 이슈";
    }

    return "내 할 일";
  }, [currentUser]);

  const emptyTaskMessage = useMemo(() => {
    if (!currentUser) {
      return "로그인 후 확인할 수 있습니다.";
    }

    if (currentUser.role === "PL") {
      return "검토할 이슈가 없습니다.";
    }

    if (currentUser.role === "TESTER") {
      return "확인할 이슈가 없습니다.";
    }

    if (currentUser.role === "ADMIN") {
      return "등록된 이슈가 없습니다.";
    }

    return "할당된 이슈가 없습니다.";
  }, [currentUser]);

  const recentIssues: RecentIssueRow[] = useMemo(() => {
    return issues.slice(0, 5).map((issue) => ({
      id: issue.id,
      title: issue.title,
      status: issue.status,
      priority: issue.priority,
      assignee: issue.assignee?.username ?? "-",
      lastActivity: issue.reportedDate ? issue.reportedDate.slice(0, 10) : "-",
    }));
  }, [issues]);

  const handleProjectChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const nextProjectId = Number(event.target.value);
    navigate(`/dashboard/${nextProjectId}`);
  };

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

  if (projects.length === 0) {
    return (
      <section className="dashboard-page">
        <div className="dashboard-top-bar">
          <h2>대시보드</h2>
        </div>

        <p>등록된 프로젝트가 없습니다.</p>
      </section>
    );
  }

  return (
    <section className="dashboard-page">
      <div className="dashboard-top-bar">
        <div>
          <h2>대시보드</h2>
          {selectedProject?.description && (
            <p className="dashboard-project-description">
              {selectedProject.description}
            </p>
          )}
        </div>

        <div className="dashboard-project-selector">
          <select
            value={numericProjectId ?? ""}
            onChange={handleProjectChange}
          >
            {projects.map((project) => (
              <option key={project.id} value={project.id}>
                {project.name}
              </option>
            ))}
          </select>
        </div>
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
          <div className="panel-title">{myTaskTitle}</div>

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
            <p className="dashboard-empty-message">{emptyTaskMessage}</p>
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