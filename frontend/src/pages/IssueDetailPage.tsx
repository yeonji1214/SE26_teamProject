import { useParams } from "react-router-dom";

function IssueDetailPage() {
  const { issueId } = useParams();

  return (
    <section className="page">
      <h2>Issue Detail</h2>
      <p>이슈 상세 화면입니다.</p>
      <p>Issue ID: {issueId}</p>
    </section>
  );
}

export default IssueDetailPage;