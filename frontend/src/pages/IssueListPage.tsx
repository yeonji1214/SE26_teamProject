import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getIssues } from "../api/issueApi";
import IssueFilterPanel from "../components/IssueFilterPanel";
import IssueTable, { type IssueListRow } from "../components/IssueTable";
import type { Issue, IssuePriority, IssueStatus } from "../types/issue";

function IssueListPage() {
  const navigate = useNavigate();

  const [issues, setIssues] = useState<Issue[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const [projectFilter, setProjectFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState<IssueStatus | "">("");
  const [reporterFilter, setReporterFilter] = useState("");
  const [assigneeFilter, setAssigneeFilter] = useState("");
  const [priorityFilter, setPriorityFilter] = useState<IssuePriority | "">("");
  const [keyword, setKeyword] = useState("");

  useEffect(() => {
    loadIssues();
  }, []);

  const loadIssues = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const data = await getIssues();
      setIssues(data);
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

  const filteredIssues = useMemo(() => {
    return issues.filter((issue) => {
      const projectName = `project${issue.projectId}`;
      const reporter = issue.reporter.username;
      const assignee = issue.assignee?.username ?? "-";

      const matchesProject =
        !projectFilter ||
        projectName.toLowerCase().includes(projectFilter.toLowerCase());

      const matchesStatus = !statusFilter || issue.status === statusFilter;

      const matchesReporter =
        !reporterFilter ||
        reporter.toLowerCase().includes(reporterFilter.toLowerCase());

      const matchesAssignee =
        !assigneeFilter ||
        assignee.toLowerCase().includes(assigneeFilter.toLowerCase());

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
    project: `project${issue.projectId}`,
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