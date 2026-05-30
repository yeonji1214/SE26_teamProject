import type { IssuePriority, IssueStatus } from "../types/issue";

export interface RecentIssueRow {
  id: number;
  title: string;
  status: IssueStatus;
  priority: IssuePriority;
  assignee: string;
  lastActivity: string;
}

interface RecentIssueTableProps {
  issues: RecentIssueRow[];
  onSelectIssue: (issueId: number) => void;
}

function RecentIssueTable({ issues, onSelectIssue }: RecentIssueTableProps) {
  return (
    <div className="recent-issue-table-wrapper">
      <div className="panel-title">최근 이슈</div>

      <table className="recent-issue-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>제목</th>
            <th>상태</th>
            <th>우선순위</th>
            <th>담당자</th>
            <th>최근 활동</th>
          </tr>
        </thead>

        <tbody>
          {issues.length > 0 ? (
            issues.map((issue) => (
              <tr key={issue.id} onClick={() => onSelectIssue(issue.id)}>
                <td>{issue.id}</td>
                <td>{issue.title}</td>
                <td>{issue.status}</td>
                <td>{issue.priority}</td>
                <td>{issue.assignee}</td>
                <td>{issue.lastActivity}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={6} className="dashboard-empty-message">
                최근 이슈가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default RecentIssueTable;