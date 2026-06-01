import type { IssuePriority, IssueStatus } from "../types/issue";

export interface IssueListRow {
    id: number;
    project: string;
    title: string;
    status: IssueStatus;
    priority: IssuePriority;
    reporter: string;
    assignee: string;
    createdAt: string;
}

interface IssueTableProps {
    issues: IssueListRow[];
    onSelectIssue: (issueId: number) => void;
}

function IssueTable({ issues, onSelectIssue }: IssueTableProps) {
  return (
    <div className="issue-table-wrapper">
      <table className="issue-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>제목</th>
            <th>상태</th>
            <th>우선순위</th>
            <th>리포터</th>
            <th>담당자</th>
            <th>등록일</th>
          </tr>
        </thead>

        <tbody>
          {issues.length > 0 ? (
            issues.map((issue) => (
              <tr key={issue.id} onClick={() => onSelectIssue(issue.id)}>
                <td>{issue.id}</td>
                <td>{issue.title}</td>
                <td>
                  <span className={`status-badge ${issue.status.toLowerCase()}`}>
                    {issue.status}
                  </span>
                </td>
                <td>{issue.priority}</td>
                <td>{issue.reporter}</td>
                <td>{issue.assignee}</td>
                <td>{issue.createdAt}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td className="empty-table-message" colSpan={7}>
                조건에 맞는 이슈가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default IssueTable;
