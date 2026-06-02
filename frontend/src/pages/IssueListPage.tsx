import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getIssues } from "../api/issueApi";
import { getProjects } from "../api/projectApi";
import { getUsers } from "../api/userApi";
import IssueFilterPanel from "../components/IssueFilterPanel";
import IssueTable, { type IssueListRow } from "../components/IssueTable";
import type { Issue, IssuePriority, IssueStatus } from "../types/issue";
import type { Project } from "../types/project";
import type { User } from "../types/user";

function IssueListPage() {
  const navigate = useNavigate();

  const [issues, setIssues] = useState<Issue[]>([]);
  const [projects, setProjects] = useState<Project[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const [projectFilter, setProjectFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState<IssueStatus | "">("");
  const [reporterFilter, setReporterFilter] = useState("");
  const [assigneeFilter, setAssigneeFilter] = useState("");
  const [priorityFilter, setPriorityFilter] = useState<IssuePriority | "">("");
  const [keyword, setKeyword] = useState("");

  useEffect(() => {
    loadPageData();
  }, []);

  const loadPageData = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const [issueData, projectData, userData] = await Promise.all([
        getIssues(),
        getProjects(),
        getUsers(),
      ]);

      setIssues(issueData);
      setProjects(projectData);
      setUsers(userData);
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "이슈 목록을 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const projectOptions = useMemo(() => {
    return projects
      .slice()
      .sort((a, b) => a.id - b.id)
      .map((project) => ({
        id: String(project.id),
        name: project.name,
      }));
  }, [projects]);

  const reporterOptions = useMemo(() => {
    return users
      .slice()
      .filter((user) => user.role === "TESTER" || user.role === "ADMIN")
      .sort((a, b) => a.username.localeCompare(b.username))
      .map((user) => ({
        id: user.username,
        name: user.username,
      }));
  }, [users]);

  const assigneeOptions = useMemo(() => {
    return users
      .slice()
      .filter((user) => user.role === "DEV")
      .sort((a, b) => a.username.localeCompare(b.username))
      .map((user) => ({
        id: user.username,
        name: user.username,
      }));
  }, [users]);

  const getProjectName = (projectId: number) => {
    const project = projects.find((item) => item.id === projectId);
    return project?.name ?? `project${projectId}`;
  };

  const filteredIssues = useMemo(() => {
    return issues.filter((issue) => {
      const reporter = issue.reporter.username;
      const assignee = issue.assignee?.username ?? "";

      const matchesProject =
        !projectFilter || String(issue.projectId) === projectFilter;

      const matchesStatus = !statusFilter || issue.status === statusFilter;

      const matchesReporter = !reporterFilter || reporter === reporterFilter;

      const matchesAssignee = !assigneeFilter || assignee === assigneeFilter;

      const matchesPriority =
        !priorityFilter || issue.priority === priorityFilter;

      const matchesKeyword =
        !keyword ||
        issue.title.toLowerCase().includes(keyword.toLowerCase()) ||
        issue.description.toLowerCase().includes(keyword.toLowerCase());

      return (
        matchesProject &&
        matchesStatus &&
        matchesReporter &&
        matchesAssignee &&
        matchesPriority &&
        matchesKeyword
      );
    });
  }, [
    issues,
    projectFilter,
    statusFilter,
    reporterFilter,
    assigneeFilter,
    priorityFilter,
    keyword,
  ]);

  const rows: IssueListRow[] = filteredIssues.map((issue) => ({
    id: issue.id,
    project: getProjectName(issue.projectId),
    title: issue.title,
    status: issue.status,
    priority: issue.priority,
    reporter: issue.reporter.username,
    assignee: issue.assignee?.username ?? "-",
    createdAt: issue.reportedDate.slice(0, 10),
  }));

  const resetFilters = () => {
    setProjectFilter("");
    setStatusFilter("");
    setReporterFilter("");
    setAssigneeFilter("");
    setPriorityFilter("");
    setKeyword("");
  };

  return (
    <section className="page-section">
      <div className="page-header-row">
        <div>
          <h2>이슈 목록</h2>
          <p>백엔드 API에서 불러온 이슈 목록을 조회합니다.</p>
        </div>

        <button
          type="button"
          className="primary-button"
          onClick={() => navigate("/issues/new")}
        >
          + 이슈 등록
        </button>
      </div>

      <IssueFilterPanel
        projectOptions={projectOptions}
        reporterOptions={reporterOptions}
        assigneeOptions={assigneeOptions}
        projectFilter={projectFilter}
        statusFilter={statusFilter}
        reporterFilter={reporterFilter}
        assigneeFilter={assigneeFilter}
        priorityFilter={priorityFilter}
        keyword={keyword}
        onProjectFilterChange={setProjectFilter}
        onStatusFilterChange={setStatusFilter}
        onReporterFilterChange={setReporterFilter}
        onAssigneeFilterChange={setAssigneeFilter}
        onPriorityFilterChange={setPriorityFilter}
        onKeywordChange={setKeyword}
        onReset={resetFilters}
      />

      {isLoading && <p>이슈 목록을 불러오는 중입니다.</p>}
      {errorMessage && <p className="error-message">{errorMessage}</p>}

      {!isLoading && !errorMessage && (
        <IssueTable
          issues={rows}
          onSelectIssue={(issueId) => navigate(`/issues/${issueId}`)}
        />
      )}
    </section>
  );
}

export default IssueListPage;