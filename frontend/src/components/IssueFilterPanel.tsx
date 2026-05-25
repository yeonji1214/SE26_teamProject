import type { IssuePriority, IssueStatus } from "../types/issue";

interface IssueFilterPanelProps {
  projectFilter: string;
  statusFilter: IssueStatus | "";
  reporterFilter: string;
  assigneeFilter: string;
  priorityFilter: IssuePriority | "";
  keyword: string;

  onProjectFilterChange: (value: string) => void;
  onStatusFilterChange: (value: IssueStatus | "") => void;
  onReporterFilterChange: (value: string) => void;
  onAssigneeFilterChange: (value: string) => void;
  onPriorityFilterChange: (value: IssuePriority | "") => void;
  onKeywordChange: (value: string) => void;

  onReset: () => void;
}

function IssueFilterPanel({
  projectFilter,
  statusFilter,
  reporterFilter,
  assigneeFilter,
  priorityFilter,
  keyword,
  onProjectFilterChange,
  onStatusFilterChange,
  onReporterFilterChange,
  onAssigneeFilterChange,
  onPriorityFilterChange,
  onKeywordChange,
  onReset,
}: IssueFilterPanelProps) {
  return (
    <div className="issue-filter-panel">
      <div className="filter-row">
        <label className="filter-field">
          <span>프로젝트</span>
          <input
            value={projectFilter}
            onChange={(event) => onProjectFilterChange(event.target.value)}
            placeholder="project1"
          />
        </label>

        <label className="filter-field">
          <span>상태</span>
          <select
            value={statusFilter}
            onChange={(event) =>
              onStatusFilterChange(event.target.value as IssueStatus | "")
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
            onChange={(event) => onReporterFilterChange(event.target.value)}
            placeholder="tester1"
          />
        </label>

        <label className="filter-field">
          <span>담당자</span>
          <input
            value={assigneeFilter}
            onChange={(event) => onAssigneeFilterChange(event.target.value)}
            placeholder="dev1"
          />
        </label>

        <label className="filter-field">
          <span>우선순위</span>
          <select
            value={priorityFilter}
            onChange={(event) =>
              onPriorityFilterChange(event.target.value as IssuePriority | "")
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
          onChange={(event) => onKeywordChange(event.target.value)}
          placeholder="이슈 제목 검색"
        />

        <button type="button" className="primary-button">
          검색
        </button>

        <button type="button" className="secondary-button" onClick={onReset}>
          초기화
        </button>
      </div>
    </div>
  );
}

export default IssueFilterPanel;