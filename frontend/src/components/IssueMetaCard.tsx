import type { Issue } from "../types/issue";

interface IssueMetaCardProps {
  issue: Issue;
}

function IssueMetaCard({ issue }: IssueMetaCardProps) {
  return (
    <article className="issue-detail-card issue-meta-card">
      <h3>이슈 정보</h3>

      <dl className="issue-meta-list">
        <dt>프로젝트</dt>
        <dd>project{issue.projectId}</dd>

        <dt>리포터</dt>
        <dd>{issue.reporter.displayName}</dd>

        <dt>등록일</dt>
        <dd>{new Date(issue.reportedDate).toLocaleString()}</dd>

        <dt>담당자</dt>
        <dd>{issue.assignee?.displayName ?? "미배정"}</dd>

        <dt>수행자</dt>
        <dd>{issue.fixer?.displayName ?? "-"}</dd>

        <dt>상태</dt>
        <dd>
          <span className={`status-badge ${issue.status.toLowerCase()}`}>
            {issue.status}
          </span>
        </dd>

        <dt>우선순위</dt>
        <dd>{issue.priority}</dd>
      </dl>
    </article>
  );
}

export default IssueMetaCard;