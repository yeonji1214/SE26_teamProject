import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import IssueTable, { type IssueListRow } from "../components/IssueTable";
import type { IssuePriority, IssueStatus } from "../types/issue";

const mockIssues: IssueListRow[] = [
  {
    id: 1,
    project: "project1",
    title: "로그인 후 이슈 목록이 보이지 않음",
    status: "ASSIGNED",
    priority: "MAJOR",
    reporter: "tester1",
    assignee: "dev1",
    createdAt: "2026-05-22",
  },
  {
    id: 2,
    project: "project1",
    title: "이슈 등록 시 priority 기본값 확인 필요",
    status: "NEW",
    priority: "MAJOR",
    reporter: "tester1",
    assignee: "-",
    createdAt: "2026-05-22",
  },
  {
    id: 3,
    project: "project1",
    title: "통계 페이지 월별 이슈 수 표시 오류",
    status: "RESOLVED",
    priority: "MINOR",
    reporter: "tester1",
    assignee: "dev2",
    createdAt: "2026-05-21",
  },
];

function IssueListPage() {
  const navigate = useNavigate();

  const [projectFilter, setProjectFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState<IssueStatus | "">("");
  const [reporterFilter, setReporterFilter] = useState("");
  const [assigneeFilter, setAssigneeFilter] = useState("");
  const [priorityFilter, setPriorityFilter] = useState<IssuePriority | "">("");
  const [keyword, setKeyword] = useState("");

  const filteredIssues = useMemo(() => {
    return mockIssues.filter((issue) => {
      const matchesProject =
        !projectFilter ||
        issue.project.toLowerCase().includes(projectFilter.toLowerCase());

      const matchesStatus = !statusFilter || issue.status === statusFilter;

      const matchesReporter =
        !reporterFilter ||
        issue.reporter.toLowerCase().includes(reporterFilter.toLowerCase());

      const matchesAssignee =
        !assigneeFilter ||
        issue.assignee.toLowerCase().includes(assigneeFilter.toLowerCase());

      const matchesPriority =
        !priorityFilter || issue.priority === priorityFilter;

      const matchesKeyword =
        !keyword ||
        issue.title.toLowerCase().includes(keyword.toLowerCase());

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
    projectFilter,
    statusFilter,
    reporterFilter,
    assigneeFilter,
    priorityFilter,
    keyword,
  ]);

  const resetFilters = () => {
    setProjectFilter("");
    setStatusFilter("");
    setReporterFilter("");
    setAssigneeFilter("");
    setPriorityFilter("");
    setKeyword("");
  };

  return (
    <section className="issue-list-page">
      <div className="issue-list-header">
        <h2>이슈 목록</h2>

        <button
          type="button"
          className="primary-button"
          onClick={() => navigate("/issues/new")}
        >
          + 이슈 등록
        </button>
      </div>

      <div className="issue-filter-panel">
        <div className="filter-row">
          <label className="filter-field">
            <span>프로젝트</span>
            <input
              value={projectFilter}
              onChange={(event) => setProjectFilter(event.target.value)}
              placeholder="project1"
            />
          </label>

          <label className="filter-field">
            <span>상태</span>
            <select
              value={statusFilter}
              onChange={(event) =>
                setStatusFilter(event.target.value as IssueStatus | "")
              }
            >
              <option value="">전체</option>
              <option value="NEW">NEW</option>
              <option value="ASSIGNED">ASSIGNED</option>
              <option value="FIXED">FIXED</option>
              <option value="RESOLVED">RESOLVED</option>
              <option value="CLOSED">CLOSED</option>
              <option value="REOPENED">REOPENED</option>
            </select>
          </label>

          <label className="filter-field">
            <span>리포터</span>
            <input
              value={reporterFilter}
              onChange={(event) => setReporterFilter(event.target.value)}
              placeholder="tester1"
            />
          </label>

          <label className="filter-field">
            <span>담당자</span>
            <input
              value={assigneeFilter}
              onChange={(event) => setAssigneeFilter(event.target.value)}
              placeholder="dev1"
            />
          </label>

          <label className="filter-field">
            <span>우선순위</span>
            <select
              value={priorityFilter}
              onChange={(event) =>
                setPriorityFilter(event.target.value as IssuePriority | "")
              }
            >
              <option value="">전체</option>
              <option value="BLOCKER">BLOCKER</option>
              <option value="CRITICAL">CRITICAL</option>
              <option value="MAJOR">MAJOR</option>
              <option value="MINOR">MINOR</option>
              <option value="TRIVIAL">TRIVIAL</option>
            </select>
          </label>
        </div>

        <div className="search-row">
          <input
            className="issue-search-input"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="이슈 제목 검색"
          />

          <button type="button" className="primary-button">
            검색
          </button>

          <button
            type="button"
            className="secondary-button"
            onClick={resetFilters}
          >
            초기화
          </button>
        </div>
      </div>

      <IssueTable
        issues={filteredIssues}
        onSelectIssue={(issueId) => navigate(`/issues/${issueId}`)}
      />
    </section>
  );
}

export default IssueListPage;