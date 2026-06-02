import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getIssueById, updateIssueStatus } from "../api/issueApi";
import { getAssigneeRecommendations } from "../api/recommendationApi";
import CommentList from "../components/CommentListPanel";
import IssueActionPanel from "../components/IssueActionPanel";
import IssueDescriptionCard from "../components/IssueDescriptionCard";
import IssueMetaCard from "../components/IssueMetaCard";
import type { Issue, IssueStatus } from "../types/issue";
import type { AssigneeRecommendation } from "../types/recommendation";
import { getCurrentUser } from "../utils/authStorage";

function IssueDetailPage() {
  const navigate = useNavigate();
  const { issueId } = useParams();

  const [issue, setIssue] = useState<Issue | null>(null);
  const [recommendations, setRecommendations] = useState<
    AssigneeRecommendation[]
  >([]);
  const [nextStatus, setNextStatus] = useState<IssueStatus>("NEW");
  const [comment, setComment] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    if (!issueId) {
      return;
    }

    loadIssue(Number(issueId));
  }, [issueId]);

  const loadIssue = async (id: number) => {
    setErrorMessage("");

    try {
      const data = await getIssueById(id);
      setIssue(data);
      setNextStatus(data.status);

      const recommendationData = await getAssigneeRecommendations(id, 3);
      setRecommendations(recommendationData);
    } catch (error) {
      setIssue(null);
      setRecommendations([]);
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "이슈 정보를 불러오지 못했습니다."
      );
    }
  };

  const handleApplyStatus = async () => {
    if (!issue) {
      return;
    }

    const currentUser = getCurrentUser();

    try {
      const updatedIssue = await updateIssueStatus(issue.id, {
        status: nextStatus,
        actorId: currentUser?.id,
        comment,
      });

      setIssue(updatedIssue);
      setComment("");

      const recommendationData = await getAssigneeRecommendations(issue.id, 3);
      setRecommendations(recommendationData);
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : "상태 변경 중 오류가 발생했습니다."
      );
    }
  };

  if (!issueId) {
    return (
      <section className="page-section">
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
      <section className="page-section">
        <p>{errorMessage || "이슈 정보를 불러오는 중입니다."}</p>
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
    <section className="page-section">
      <button
        type="button"
        className="text-button"
        onClick={() => navigate("/issues")}
      >
        ← 이슈 목록으로
      </button>

      <div className="page-header-row">
        <div>
          <h2>{issue.title}</h2>
          <p>Issue #{issue.id}</p>
        </div>
      </div>

      <div className="issue-detail-layout">
        <div>
          <IssueDescriptionCard description={issue.description} />
          <CommentList comments={issue.comments} />
        </div>

        <aside>
          <IssueMetaCard issue={issue} />

          <IssueActionPanel
            nextStatus={nextStatus}
            comment={comment}
            onNextStatusChange={setNextStatus}
            onCommentChange={setComment}
            onApplyStatus={handleApplyStatus}
          />

          <article className="issue-side-card">
            <h3>추천 담당자</h3>
            {recommendations.length > 0 ? (
              <ul>
                {recommendations.map((recommendation) => (
                  <li key={recommendation.assignee.id}>
                    <strong>{recommendation.assignee.username}</strong>
                    <br />
                    score: {recommendation.score}
                    <br />
                    matched issues: {recommendation.matchedIssueCount}
                    <br />
                    matched terms: {recommendation.matchedTerms.join(", ") || "-"}
                    <br />
                    evidence: {recommendation.evidenceIssueTitles.join(", ") || "-"}
                    <br />
                    open assigned issues: {recommendation.currentOpenAssignedIssueCount}
                    <br />
                    reason: {recommendation.explanation}
                  </li>
                ))}
              </ul>
            ) : (
              <p>추천 가능한 담당자가 없습니다.</p>
            )}
          </article>
        </aside>
      </div>
    </section>
  );
}

export default IssueDetailPage;