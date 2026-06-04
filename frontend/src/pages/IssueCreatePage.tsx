import { useNavigate } from "react-router-dom";
import { createIssue } from "../api/issueApi";
import IssueAutoInfoCard from "../components/IssueAutoInfoCard";
import IssueCreateGuide from "../components/IssueCreateGuide";
import IssueForm from "../components/IssueForm";
import type { IssueCreateRequest } from "../types/issue";
import { getCurrentUser } from "../utils/authStorage";

type IssueFormRequest = Omit<IssueCreateRequest, "reporterId">;

function IssueCreatePage() {
  const navigate = useNavigate();

  const handleCreateIssue = async (request: IssueFormRequest) => {
    const currentUser = getCurrentUser();

    if (!currentUser) {
      alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
      navigate("/login");
      return;
    }

    try {
      const createdIssue = await createIssue({
        ...request,
        reporterId: currentUser.id,
      });

      alert("이슈가 등록되었습니다.");
      navigate(`/issues/${createdIssue.id}`);
    } catch (error) {
      alert(
        error instanceof Error
          ? error.message
          : "이슈 등록 중 오류가 발생했습니다."
      );
    }
  };

  return (
    <section className="issue-create-page">
      <main className="issue-create-main">
        <h2>이슈 등록</h2>

        <IssueForm
          onSubmit={handleCreateIssue}
          onCancel={() => navigate("/issues")}
        />
      </main>

      <aside className="issue-create-side">
        <IssueCreateGuide />
        <IssueAutoInfoCard />
      </aside>
    </section>
  );
}

export default IssueCreatePage;