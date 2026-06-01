import { useNavigate } from "react-router-dom";
import { createIssue } from "../api/issueApi";
import IssueAutoInfoCard from "../components/IssueAutoInfoCard";
import IssueCreateGuide from "../components/IssueCreateGuide";
import IssueForm from "../components/IssueForm";
import type { IssueCreateRequest } from "../types/issue";

function IssueCreatePage() {
  const navigate = useNavigate();

  const handleCreateIssue = async (request: IssueCreateRequest) => {
    const createdIssue = await createIssue(request);

    alert("이슈가 등록되었습니다.");
    navigate(`/issues/${createdIssue.id}`);
  };

  return (
    <section className="issue-create-page">
      <div className="issue-create-main">
        <h2>이슈 등록</h2>

        <IssueForm
          onSubmit={handleCreateIssue}
          onCancel={() => navigate("/issues")}
        />
      </div>

      <div className="issue-create-divider" />

      <div className="issue-create-side">
        <IssueCreateGuide />
        <IssueAutoInfoCard />
      </div>
    </section>
  );
}

export default IssueCreatePage;