interface IssueDescriptionCardProps {
  description: string;
}

function IssueDescriptionCard({ description }: IssueDescriptionCardProps) {
  return (
    <article className="issue-detail-card issue-description-card">
      <h3>설명</h3>
      <p>{description}</p>
    </article>
  );
}

export default IssueDescriptionCard;