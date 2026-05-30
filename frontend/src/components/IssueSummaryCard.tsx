interface IssueSummaryCardProps {
  label: string;
  count: number;
}

function IssueSummaryCard({ label, count }: IssueSummaryCardProps) {
  return (
    <article className="issue-summary-card">
      <span>{label}</span>
      <strong>{count}</strong>
    </article>
  );
}

export default IssueSummaryCard;