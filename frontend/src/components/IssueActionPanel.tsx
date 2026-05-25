import type { IssueStatus } from "../types/issue";

interface IssueActionPanelProps {
  nextStatus: IssueStatus;
  comment: string;
  onNextStatusChange: (status: IssueStatus) => void;
  onCommentChange: (comment: string) => void;
  onApplyStatus: () => void;
}

function IssueActionPanel({
  nextStatus,
  comment,
  onNextStatusChange,
  onCommentChange,
  onApplyStatus,
}: IssueActionPanelProps) {
  return (
    <article className="issue-detail-card issue-action-panel">
      <h3>작업</h3>

      <label className="detail-form-field">
        <span>상태 변경</span>
        <select
          value={nextStatus}
          onChange={(event) =>
            onNextStatusChange(event.target.value as IssueStatus)
          }
        >
          <option value="NEW">NEW</option>
          <option value="ASSIGNED">ASSIGNED</option>
          <option value="FIXED">FIXED</option>
          <option value="RESOLVED">RESOLVED</option>
          <option value="CLOSED">CLOSED</option>
          <option value="REOPENED">REOPENED</option>
        </select>
      </label>

      <label className="detail-form-field">
        <span>코멘트</span>
        <textarea
          value={comment}
          onChange={(event) => onCommentChange(event.target.value)}
          placeholder="상태 변경 사유 또는 작업 내용을 입력하세요."
        />
      </label>

      <button type="button" className="primary-button" onClick={onApplyStatus}>
        상태 변경
      </button>
    </article>
  );
}

export default IssueActionPanel;