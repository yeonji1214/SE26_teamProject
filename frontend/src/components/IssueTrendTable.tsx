interface IssueTrendRow {
  period: string;
  created: number;
  resolved: number;
  closed: number;
}

interface IssueTrendTableProps {
  rows: IssueTrendRow[];
}

function IssueTrendTable({ rows }: IssueTrendTableProps) {
  return (
    <article className="stat-panel">
      <div className="panel-title">월별 이슈 트렌드</div>

      <table className="issue-trend-table">
        <thead>
          <tr>
            <th>기간</th>
            <th>생성</th>
            <th>해결</th>
            <th>종료</th>
          </tr>
        </thead>

        <tbody>
          {rows.map((row) => (
            <tr key={row.period}>
              <td>{row.period}</td>
              <td>{row.created}</td>
              <td>{row.resolved}</td>
              <td>{row.closed}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </article>
  );
}

export default IssueTrendTable;