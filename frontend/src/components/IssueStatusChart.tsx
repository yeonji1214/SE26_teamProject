interface IssueStatusChartItem {
  label: string;
  count: number;
}

interface IssueStatusChartProps {
  items: IssueStatusChartItem[];
}

function IssueStatusChart({ items }: IssueStatusChartProps) {
  const maxCount = Math.max(...items.map((item) => item.count), 1);

  return (
    <article className="stat-panel">
      <div className="panel-title">상태별 이슈 분포</div>

      <div className="status-chart-list">
        {items.map((item) => {
          const width = `${(item.count / maxCount) * 100}%`;

          return (
            <div key={item.label} className="status-chart-row">
              <div className="status-chart-label">
                <span>{item.label}</span>
                <strong>{item.count}</strong>
              </div>

              <div className="status-chart-bar-track">
                <div className="status-chart-bar" style={{ width }} />
              </div>
            </div>
          );
        })}
      </div>
    </article>
  );
}

export default IssueStatusChart;