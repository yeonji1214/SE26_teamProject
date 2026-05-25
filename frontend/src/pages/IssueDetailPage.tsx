import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import CommentList from "../components/CommentListPanel";
import IssueActionPanel from "../components/IssueActionPanel";
import IssueDescriptionCard from "../components/IssueDescriptionCard";
import IssueMetaCard from "../components/IssueMetaCard";
import { getIssueById, updateIssueStatus } from "../api/issueApi";
import type { Issue, IssueStatus } from "../types/issue";

function IssueDetailPage() {
  const navigate = useNavigate();
  const { issueId } = useParams();

  const [issue, setIssue] = useState<Issue | null>(null);
  const [nextStatus, setNextStatus] = useState<IssueStatus>("NEW");
  const [comment, setComment] = useState("");

  useEffect(() => {
    if (!issueId) {
      return;
    }

    getIssueById(Number(issueId))
      .then((data) => {
        setIssue(data);
        setNextStatus(data.status);
      })
      .catch(() => {
        setIssue(null);
      });
  }, [issueId]);

  const handleApplyStatus = async () => {
    if (!issue) {
      return;
    }

    const updatedIssue = await updateIssueStatus(issue.id, {
      status: nextStatus,
      comment,
    });

    setIssue(updatedIssue);
    setComment("");
  };

  if (!issueId) {
    return (
      <section className="issue-detail-page">
        <p>이슈 ID가 올바르지 않습니다.</p>
        <button
          type="button"
          className="secondary-button"
          onClick={() => navigate("/issues")}
        >
          목록으로
        </button>
      </section>
    );
  }

  if (!issue) {
    return (
      <section className="issue-detail-page">
        <p>이슈 정보를 불러오는 중이거나 해당 이슈가 없습니다.</p>
        <button
          type="button"
          className="secondary-button"
          onClick={() => navigate("/issues")}
        >
          목록으로
        </button>
      </section>
    );
  }

  return (
    <section className="issue-detail-page">
      <div className="issue-detail-top-bar">
        <button
          type="button"
          className="text-button"
          onClick={() => navigate("/issues")}
        >
          ← 이슈 목록으로
        </button>
      </div>

      <div className="issue-detail-header">
        <div>
          <h2>{issue.title}</h2>
          <p>Issue #{issue.id}</p>
        </div>

        <div className="issue-detail-header-actions">
          <button type="button" className="secondary-button">
            수정
          </button>
          <button type="button" className="danger-button">
            삭제
          </button>
        </div>
      </div>

      <div className="issue-detail-grid">
        <IssueMetaCard issue={issue} />

        <IssueDescriptionCard description={issue.description} />

        <IssueActionPanel
          nextStatus={nextStatus}
          comment={comment}
          onNextStatusChange={setNextStatus}
          onCommentChange={setComment}
          onApplyStatus={handleApplyStatus}
        />
      </div>

      <CommentList comments={issue.comments} />
    </section>
  );
}

export default IssueDetailPage;