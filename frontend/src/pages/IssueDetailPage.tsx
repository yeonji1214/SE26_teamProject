import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  addIssueComment,
  getIssueById,
  updateIssueStatus,
} from "../api/issueApi";
import { getAssigneeRecommendations } from "../api/recommendationApi";
import { getUsers } from "../api/userApi";
import CommentList from "../components/CommentListPanel";
import IssueDescriptionCard from "../components/IssueDescriptionCard";
import IssueMetaCard from "../components/IssueMetaCard";
import Modal from "../components/Modal";
import type { Issue, IssueStatus } from "../types/issue";
import type { AssigneeRecommendation } from "../types/recommendation";
import type { AccountRole, User } from "../types/user";
import { getCurrentUser } from "../utils/authStorage";

type ModalType = "comment" | "status" | "assign" | null;

const STATUS_OPTIONS: IssueStatus[] = [
  "NEW",
  "ASSIGNED",
  "FIXED",
  "RESOLVED",
  "CLOSED",
  "REOPENED",
];

function getAllowedStatuses(
  role: AccountRole | undefined,
  currentStatus: IssueStatus
): IssueStatus[] {
  if (!role) {
    return [];
  }

  if (role === "ADMIN") {
    return STATUS_OPTIONS;
  }

  if (role === "PL") {
    if (currentStatus === "NEW") return ["ASSIGNED"];
    if (currentStatus === "RESOLVED") return ["CLOSED"];
    return ["ASSIGNED", "CLOSED", "REOPENED"];
  }

  if (role === "DEV") {
    if (currentStatus === "ASSIGNED") return ["FIXED"];
    return ["FIXED"];
  }

  if (role === "TESTER") {
    if (currentStatus === "FIXED") return ["RESOLVED"];
    return ["RESOLVED", "REOPENED"];
  }

  return [];
}

function IssueDetailPage() {
  const navigate = useNavigate();
  const { issueId } = useParams();

  const [issue, setIssue] = useState<Issue | null>(null);
  const [recommendations, setRecommendations] = useState<
    AssigneeRecommendation[]
  >([]);
  const [users, setUsers] = useState<User[]>([]);
  const [modalType, setModalType] = useState<ModalType>(null);

  const [nextStatus, setNextStatus] = useState<IssueStatus>("NEW");
  const [commentText, setCommentText] = useState("");
  const [statusComment, setStatusComment] = useState("");
  const [assignComment, setAssignComment] = useState("");
  const [selectedAssigneeId, setSelectedAssigneeId] = useState("");

  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const currentUser = useMemo(() => getCurrentUser(), []);

  const canAddComment = Boolean(currentUser);

  const canChangeStatus =
    currentUser?.role === "ADMIN" ||
    currentUser?.role === "PL" ||
    currentUser?.role === "DEV" ||
    currentUser?.role === "TESTER";

  const canAssignIssue =
    currentUser?.role === "ADMIN" || currentUser?.role === "PL";

  

  const allowedStatuses = useMemo(() => {
    if (!issue) {
      return [];
    }

    return getAllowedStatuses(currentUser?.role, issue.status);
  }, [currentUser?.role, issue]);

  const assigneeCandidates = useMemo(() => {
    return users.filter((user) => user.role === "DEV");
  }, [users]);

  useEffect(() => {
    if (!issueId) {
      setIsLoading(false);
      return;
    }

    loadIssue(Number(issueId));
  }, [issueId]);

  useEffect(() => {
    if (!canAssignIssue) {
      return;
    }

    getUsers()
      .then(setUsers)
      .catch(() => setUsers([]));
  }, [canAssignIssue]);

  const loadIssue = async (id: number) => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const data = await getIssueById(id);
      setIssue(data);
      setNextStatus(data.status);

      await refreshRecommendations(id);
    } catch (error) {
      setIssue(null);
      setRecommendations([]);
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "이슈 정보를 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const refreshRecommendations = async (id: number) => {
    try {
      const recommendationData = await getAssigneeRecommendations(id, 3);
      setRecommendations(recommendationData);
    } catch {
      setRecommendations([]);
    }
  };

  const closeModal = () => {
    setModalType(null);
    setCommentText("");
    setStatusComment("");
    setAssignComment("");
  };

  const openCommentModal = () => {
    setCommentText("");
    setModalType("comment");
  };

  const openStatusModal = () => {
    if (!issue) {
      return;
    }

    const defaultStatus = allowedStatuses.includes(issue.status)
      ? issue.status
      : allowedStatuses[0] ?? issue.status;

    setNextStatus(defaultStatus);
    setStatusComment("");
    setModalType("status");
  };

  const openAssignModal = () => {
    const firstRecommendedAssigneeId =
      recommendations[0]?.assignee.id.toString() ?? "";
    const firstDevId = assigneeCandidates[0]?.id.toString() ?? "";

    setSelectedAssigneeId(firstRecommendedAssigneeId || firstDevId);
    setAssignComment("");
    setModalType("assign");
  };

  const handleAddComment = async () => {
    if (!issue) {
      return;
    }

    if (!currentUser) {
      alert("로그인 정보가 없습니다.");
      return;
    }

    const trimmedComment = commentText.trim();

    if (!trimmedComment) {
      alert("코멘트를 입력해 주세요.");
      return;
    }

    try {
      const updatedIssue = await addIssueComment(
        issue.id,
        currentUser.id,
        trimmedComment
      );

      setIssue(updatedIssue);
      closeModal();
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : "코멘트 추가 중 오류가 발생했습니다."
      );
    }
  };

  const handleApplyStatus = async () => {
    if (!issue) {
      return;
    }

    if (!currentUser) {
      alert("로그인 정보가 없습니다.");
      return;
    }

    if (!nextStatus) {
      alert("변경할 상태를 선택해 주세요.");
      return;
    }

    try {
      const updatedIssue = await updateIssueStatus(issue.id, {
        status: nextStatus,
        actorId: currentUser.id,
        comment: statusComment.trim() || undefined,
      });

      setIssue(updatedIssue);
      closeModal();
      await refreshRecommendations(issue.id);
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : "상태 변경 중 오류가 발생했습니다."
      );
    }
  };

  const handleAssignIssue = async () => {
    if (!issue) {
      return;
    }

    if (!currentUser) {
      alert("로그인 정보가 없습니다.");
      return;
    }

    if (!selectedAssigneeId) {
      alert("담당자를 선택해 주세요.");
      return;
    }

    try {
      const updatedIssue = await updateIssueStatus(issue.id, {
        status: "ASSIGNED",
        actorId: currentUser.id,
        comment: assignComment.trim() || undefined,
        assigneeId: Number(selectedAssigneeId),
      } as Parameters<typeof updateIssueStatus>[1] & { assigneeId: number });

      setIssue(updatedIssue);
      setNextStatus(updatedIssue.status);
      closeModal();
      await refreshRecommendations(issue.id);
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : "담당자 지정 중 오류가 발생했습니다."
      );
    }
  };

  if (!issueId) {
    return (
      <section className="page-section issue-detail-page">
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

  if (isLoading) {
    return (
      <section className="page-section issue-detail-page">
        <p>이슈 정보를 불러오는 중입니다.</p>
      </section>
    );
  }

  if (!issue) {
    return (
      <section className="page-section issue-detail-page">
        <p>{errorMessage || "이슈 정보를 불러오지 못했습니다."}</p>
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
    <section className="page-section issue-detail-page">
      <div className="issue-detail-top-bar">
        <button
          type="button"
          className="text-button issue-back-button"
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


      </div>

      <div className="issue-detail-grid">
        <IssueMetaCard issue={issue} />

        <IssueDescriptionCard description={issue.description} />

        <article className="issue-detail-card issue-action-card">
          <h3>작업</h3>

          <div className="issue-action-buttons">
            {canAddComment && (
              <button
                type="button"
                className="secondary-button full-width-button"
                onClick={openCommentModal}
              >
                코멘트 추가
              </button>
            )}

            {canChangeStatus && (
              <button
                type="button"
                className="secondary-button full-width-button"
                onClick={openStatusModal}
                disabled={allowedStatuses.length === 0}
              >
                상태 변경
              </button>
            )}

            {canAssignIssue && (
              <button
                type="button"
                className="primary-button full-width-button"
                onClick={openAssignModal}
              >
                담당자 지정
              </button>
            )}
          </div>

          {canAssignIssue && (
            <div className="recommendation-preview">
              <h4>추천 담당자</h4>

              {recommendations.length > 0 ? (
                <ul>
                  {recommendations.slice(0, 3).map((recommendation) => (
                    <li key={recommendation.assignee.id} className="recommendation-preview-item">
                      <div className="recommendation-preview-header">
                        <strong>{recommendation.assignee.username}</strong>
                        <span>score: {recommendation.score.toFixed(1)}</span>
                      </div>

                      <p>
                        <strong>근거 이슈:</strong>{" "}
                        {recommendation.evidenceIssueTitles.length > 0
                          ? recommendation.evidenceIssueTitles[0]
                          : "없음"}
                      </p>

                      <p>
                        <strong>매칭 키워드:</strong>{" "}
                        {recommendation.matchedTerms.length > 0
                          ? recommendation.matchedTerms.join(", ")
                          : "없음"}
                      </p>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>추천 가능한 담당자가 없습니다.</p>
              )}
            </div>
          )}
        </article>
      </div>

      <div className="issue-comment-section">
        <CommentList comments={issue.comments} />
      </div>

      {modalType === "comment" && (
        <Modal title="코멘트 추가" onClose={closeModal}>
          <div className="modal-body">
            <label className="detail-form-field">
              <span>코멘트</span>
              <textarea
                value={commentText}
                onChange={(event) => setCommentText(event.target.value)}
                placeholder="코멘트를 입력하세요."
              />
            </label>
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="secondary-button"
              onClick={closeModal}
            >
              취소
            </button>
            <button
              type="button"
              className="primary-button"
              onClick={handleAddComment}
            >
              추가
            </button>
          </div>
        </Modal>
      )}

      {modalType === "status" && (
        <Modal title="상태 변경" onClose={closeModal}>
          <div className="modal-body">
            <div className="modal-info-row">
              <span>현재 상태</span>
              <strong>{issue.status}</strong>
            </div>

            <p className="modal-help-text">
              코멘트는 선택 사항입니다. 입력하면 상태 변경 이력과 함께 코멘트에 기록됩니다.
            </p>

            <label className="detail-form-field">
              <span>변경할 상태</span>
              <select
                value={nextStatus}
                onChange={(event) =>
                  setNextStatus(event.target.value as IssueStatus)
                }
              >
                {allowedStatuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </label>

            <label className="detail-form-field">
              <span>코멘트</span>
              <textarea
                value={statusComment}
                onChange={(event) => setStatusComment(event.target.value)}
                placeholder="상태 변경 사유 또는 작업 내용을 입력하세요."
              />
            </label>
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="secondary-button"
              onClick={closeModal}
            >
              취소
            </button>
            <button
              type="button"
              className="primary-button"
              onClick={handleApplyStatus}
            >
              상태 변경
            </button>
          </div>
        </Modal>
      )}

      {modalType === "assign" && (
        <Modal title="담당자 지정" onClose={closeModal}>
          <div className="modal-body">
            <div className="assign-recommendation-box">
              <h4>추천 담당자</h4>

              {recommendations.length > 0 ? (
                <ul>
                  {recommendations.map((recommendation) => (
                    <li key={recommendation.assignee.id}>
                      <button
                        type="button"
                        className="recommendation-button recommendation-button-detail"
                        onClick={() =>
                          setSelectedAssigneeId(recommendation.assignee.id.toString())
                        }
                      >
                        <div className="recommendation-preview-header">
                          <strong>{recommendation.assignee.username}</strong>
                          <span>score: {recommendation.score.toFixed(1)}</span>
                        </div>

                        <div className="recommendation-reason">
                          <p>
                            <strong>근거 이슈:</strong>{" "}
                            {recommendation.evidenceIssueTitles.length > 0
                              ? recommendation.evidenceIssueTitles.join(", ")
                              : "없음"}
                          </p>

                          <p>
                            <strong>매칭 키워드:</strong>{" "}
                            {recommendation.matchedTerms.length > 0
                              ? recommendation.matchedTerms.join(", ")
                              : "없음"}
                          </p>

                          <p>
                            <strong>매칭된 해결 이슈 수:</strong>{" "}
                            {recommendation.matchedIssueCount}
                          </p>

                          <p>
                            <strong>현재 담당 중인 미해결 이슈:</strong>{" "}
                            {recommendation.currentOpenAssignedIssueCount}
                          </p>

                          <p>
                            <strong>설명:</strong> {recommendation.explanation}
                          </p>
                        </div>
                      </button>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>추천 가능한 담당자가 없습니다.</p>
              )}
            </div>

            <label className="detail-form-field">
              <span>담당자 선택</span>
              <select
                value={selectedAssigneeId}
                onChange={(event) => setSelectedAssigneeId(event.target.value)}
              >
                <option value="">담당자를 선택하세요</option>
                {assigneeCandidates.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.username}
                  </option>
                ))}
              </select>
            </label>

            <label className="detail-form-field">
              <span>코멘트</span>
              <textarea
                value={assignComment}
                onChange={(event) => setAssignComment(event.target.value)}
                placeholder="담당자 지정 사유를 입력하세요."
              />
            </label>
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="secondary-button"
              onClick={closeModal}
            >
              취소
            </button>
            <button
              type="button"
              className="primary-button"
              onClick={handleAssignIssue}
            >
              담당자 지정
            </button>
          </div>
        </Modal>
      )}
    </section>
  );
}

export default IssueDetailPage;