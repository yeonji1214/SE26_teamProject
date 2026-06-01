import type { IssueComment } from "../types/issue";

interface CommentListProps {
  comments: IssueComment[];
}

function CommentList({ comments }: CommentListProps) {
  return (
    <article className="issue-detail-card comment-list-card">
      <h3>코멘트</h3>

      {comments.length > 0 ? (
        <div className="comment-list">
          {comments.map((comment) => (
            <div key={comment.id} className="comment-item">
              <div className="comment-header">
                <strong>{comment.author.displayName}</strong>
                <span>{new Date(comment.createdAt).toLocaleString()}</span>
              </div>

              <p>{comment.content}</p>
            </div>
          ))}
        </div>
      ) : (
        <p className="empty-comment-message">등록된 코멘트가 없습니다.</p>
      )}
    </article>
  );
}

export default CommentList;