import { useMemo } from "react";
import Modal from "./Modal";
import type { AssigneeRecommendation } from "../types/recommendation";
import type { User } from "../types/user";

interface AssignIssueModalProps {
  recommendations: AssigneeRecommendation[];
  assigneeCandidates: User[];
  selectedAssigneeId: string;
  assignComment: string;
  onSelectAssignee: (assigneeId: string) => void;
  onChangeComment: (comment: string) => void;
  onClose: () => void;
  onSubmit: () => void;
}

function AssignIssueModal({
  recommendations,
  assigneeCandidates,
  selectedAssigneeId,
  assignComment,
  onSelectAssignee,
  onChangeComment,
  onClose,
  onSubmit,
}: AssignIssueModalProps) {
  const selectedRecommendation = useMemo(() => {
    return recommendations.find(
      (recommendation) =>
        recommendation.assignee.id.toString() === selectedAssigneeId
    );
  }, [recommendations, selectedAssigneeId]);

  const selectedAssignee = useMemo(() => {
    return assigneeCandidates.find(
      (user) => user.id.toString() === selectedAssigneeId
    );
  }, [assigneeCandidates, selectedAssigneeId]);

  return (
    <Modal title="담당자 지정" onClose={onClose}>
      <div className="modal-body">
        <div className="assign-recommendation-box">
          <h4>추천 담당자</h4>

          {recommendations.length > 0 ? (
            <ul>
              {recommendations.map((recommendation) => {
                const isSelected =
                  recommendation.assignee.id.toString() === selectedAssigneeId;

                return (
                  <li key={recommendation.assignee.id}>
                    <button
                      type="button"
                      className={
                        isSelected
                          ? "recommendation-button recommendation-button-selected"
                          : "recommendation-button"
                      }
                      onClick={() =>
                        onSelectAssignee(recommendation.assignee.id.toString())
                      }
                    >
                      <div className="recommendation-button-header">
                        <div>
                          <span className="recommendation-badge">추천</span>
                          <strong>{recommendation.assignee.username}</strong>
                        </div>

                        <span>score: {recommendation.score.toFixed(1)}</span>
                      </div>

                      <div className="recommendation-button-detail">
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
                      </div>
                    </button>
                  </li>
                );
              })}
            </ul>
          ) : (
            <p>추천 가능한 담당자가 없습니다.</p>
          )}
        </div>

        <label className="detail-form-field">
          <span>담당자 선택</span>
          <select
            value={selectedAssigneeId}
            onChange={(event) => onSelectAssignee(event.target.value)}
          >
            <option value="">담당자를 선택하세요</option>
            {assigneeCandidates.map((user) => (
              <option key={user.id} value={user.id}>
                {user.username}
              </option>
            ))}
          </select>
        </label>

        {selectedAssigneeId && (
          <div className="selected-assignee-explanation">
            <h4>선택된 담당자 설명</h4>

            {selectedRecommendation ? (
              <>
                <p>
                  <strong>{selectedRecommendation.assignee.username}</strong>
                  은(는) 현재 추천 후보에 포함되어 있습니다.
                </p>

                <p>
                  유사한 해결 이슈{" "}
                  <strong>{selectedRecommendation.matchedIssueCount}</strong>
                  건과 매칭되어 추천되었습니다.
                </p>

                <p>
                  <strong>근거 이슈:</strong>{" "}
                  {selectedRecommendation.evidenceIssueTitles.length > 0
                    ? selectedRecommendation.evidenceIssueTitles.join(", ")
                    : "없음"}
                </p>

                <p>
                  <strong>매칭 키워드:</strong>{" "}
                  {selectedRecommendation.matchedTerms.length > 0
                    ? selectedRecommendation.matchedTerms.join(", ")
                    : "없음"}
                </p>

                <p>
                  <strong>현재 담당 중인 미해결 이슈:</strong>{" "}
                  {selectedRecommendation.currentOpenAssignedIssueCount}
                </p>

                <p>
                  <strong>설명:</strong>{" "}
                  {selectedRecommendation.explanation ||
                    `${selectedRecommendation.assignee.username}은(는) 유사한 해결 이슈 ${selectedRecommendation.matchedIssueCount}건과 매칭되어 추천되었습니다.`}
                </p>
              </>
            ) : (
              <>
                <p>
                  <strong>
                    {selectedAssignee?.username ?? "선택한 담당자"}
                  </strong>
                  는 현재 추천 후보에는 포함되지 않았습니다.
                </p>

                <p>
                  유사한 해결 이슈 이력이 없거나 점수가 낮아 추천 목록에
                  표시되지 않았습니다.
                </p>

                <p>수동 배정은 가능합니다.</p>
              </>
            )}
          </div>
        )}

        <label className="detail-form-field">
          <span>코멘트</span>
          <textarea
            value={assignComment}
            onChange={(event) => onChangeComment(event.target.value)}
            placeholder="담당자 지정 사유를 입력하세요."
          />
        </label>
      </div>

      <div className="modal-footer">
        <button type="button" className="secondary-button" onClick={onClose}>
          취소
        </button>
        <button type="button" className="primary-button" onClick={onSubmit}>
          담당자 지정
        </button>
      </div>
    </Modal>
  );
}

export default AssignIssueModal;