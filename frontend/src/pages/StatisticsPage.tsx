import IssueStatusChart from "../components/IssueStatusChart";
import IssueTrendTable from "../components/IssueTrendTable";
import StatisticSummaryCard from "../components/StatisticSummaryCard";

const statusStats = [
  { label: "NEW", count: 55 },
  { label: "ASSIGNED", count: 24 },
  { label: "FIXED", count: 18 },
  { label: "RESOLVED", count: 36 },
  { label: "CLOSED", count: 13 },
  { label: "REOPENED", count: 4 },
];

const monthlyTrendRows = [
  {
    period: "2026-03",
    created: 24,
    resolved: 12,
    closed: 9,
  },
  {
    period: "2026-04",
    created: 41,
    resolved: 28,
    closed: 19,
  },
  {
    period: "2026-05",
    created: 63,
    resolved: 36,
    closed: 13,
  },
];

function StatisticsPage() {
  const totalIssues = statusStats.reduce((sum, item) => sum + item.count, 0);
  const openIssues =
    statusStats.find((item) => item.label === "NEW")!.count +
    statusStats.find((item) => item.label === "ASSIGNED")!.count +
    statusStats.find((item) => item.label === "REOPENED")!.count;

  const resolvedIssues =
    statusStats.find((item) => item.label === "RESOLVED")!.count +
    statusStats.find((item) => item.label === "CLOSED")!.count;

  const resolutionRate =
    totalIssues === 0 ? 0 : Math.round((resolvedIssues / totalIssues) * 100);

  return (
    <section className="statistics-page">
      <div className="statistics-header">
        <div>
          <h2>통계</h2>
          <p>이슈 발생 현황과 처리 추이를 확인합니다.</p>
        </div>

        <button type="button" className="project-chip">
          Project 1
        </button>
      </div>

      <div className="stat-summary-grid">
        <StatisticSummaryCard
          label="전체 이슈"
          value={totalIssues}
          description="현재 프로젝트 기준"
        />

        <StatisticSummaryCard
          label="미해결 이슈"
          value={openIssues}
          description="NEW / ASSIGNED / REOPENED"
        />

        <StatisticSummaryCard
          label="해결된 이슈"
          value={resolvedIssues}
          description="RESOLVED / CLOSED"
        />

        <StatisticSummaryCard
          label="해결률"
          value={resolutionRate}
          description="%"
        />
      </div>

      <div className="statistics-grid">
        <IssueStatusChart items={statusStats} />
        <IssueTrendTable rows={monthlyTrendRows} />
      </div>
    </section>
  );
}

export default StatisticsPage;