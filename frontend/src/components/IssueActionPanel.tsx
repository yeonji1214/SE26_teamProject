import type { IssueStatus } from "../types/issue";

interface IssueActionPanelProps {
  nextStatus: IssueStatus;
  comment: string;
  onNextStatusChange: (status: IssueStatus) => void;
  onCommentChange: (comment: string) => void;
  onAddComment: () => void;
  onApplyStatus: () => void;
  onApplyStatusWithComment: () => void;
}

function IssueActionPanel({
  nextStatus,
  comment,
  onNextStatusChange,
  onCommentChange,
  onAddComment,
  onApplyStatus,
  onApplyStatusWithComment,
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
          placeholder="코멘트 또는 상태 변경 사유를 입력하세요."
        />
      </label>

      <div className="issue-action-button-group">
        <button
          type="button"
          className="secondary-button"
          onClick={onAddComment}
        >
          코멘트 추가
        </button>

        <button
          type="button"
          className="secondary-button"
          onClick={onApplyStatus}
        >
          상태만 변경
        </button>

        <button
          type="button"
          className="primary-button"
          onClick={onApplyStatusWithComment}
        >
          상태 변경 + 코멘트
        </button>
      </div>
    </article>
  );
}

export default IssueActionPanel;