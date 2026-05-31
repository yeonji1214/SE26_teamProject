interface StatisticSummaryCardProps {
  label: string;
  value: number;
  description?: string;
}

function StatisticSummaryCard({
  label,
  value,
  description,
}: StatisticSummaryCardProps) {
  return (
    <article className="stat-summary-card">
      <span>{label}</span>
      <strong>{value}</strong>
      {description && <p>{description}</p>}
    </article>
  );
}

export default StatisticSummaryCard;