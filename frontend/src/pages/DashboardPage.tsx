import { useMemo } from "react";
import { useNavigate } from "react-router-dom";
import IssueSummaryCard from "../components/IssueSummaryCard";
import RecentIssueTable, {
  type RecentIssueRow,
} from "../components/RecentIssueTable";
import type { IssuePriority, IssueStatus } from "../types/issue";

interface DashboardIssue {
  id: number;
  title: string;
  status: IssueStatus;
  priority: IssuePriority;
  assignee: string;
  lastActivity: string;
}

const mockDashboardIssues: DashboardIssue[] = [
  {
    id: 1,
    title: "로그인 후 이슈 목록이 보이지 않음",
    status: "ASSIGNED",
    priority: "MAJOR",
    assignee: "dev1",
    lastActivity: "2026-05-22",
  },
  {
    id: 2,
    title: "이슈 등록 시 priority 기본값 확인 필요",
    status: "NEW",
    priority: "MAJOR",
    assignee: "-",
    lastActivity: "2026-05-22",
  },
  {
    id: 3,
    title: "통계 페이지 월별 이슈 수 표시 오류",
    status: "RESOLVED",
    priority: "MINOR",
    assignee: "dev2",
    lastActivity: "2026-05-21",
  },
];

function DashboardPage() {
  const navigate = useNavigate();

  const summary = useMemo(() => {
    return {
      total: mockDashboardIssues.length,
      new: mockDashboardIssues.filter((issue) => issue.status === "NEW").length,
      assigned: mockDashboardIssues.filter(
        (issue) => issue.status === "ASSIGNED"
      ).length,
      resolved: mockDashboardIssues.filter(
        (issue) => issue.status === "RESOLVED"
      ).length,
      closed: mockDashboardIssues.filter((issue) => issue.status === "CLOSED")
        .length,
    };
  }, []);

  const myTasks = useMemo(() => {
    return mockDashboardIssues.filter((issue) => issue.assignee === "dev1");
  }, []);

  const recentIssues: RecentIssueRow[] = mockDashboardIssues.slice(0, 5);

  return (
    <section className="dashboard-page">
      <div className="dashboard-top-bar">
        <h2>대시보드</h2>

        <button type="button" className="project-chip">
          Project 1
        </button>
      </div>

      <div className="summary-card-row">
        <IssueSummaryCard label="전체 이슈" count={summary.total} />
        <IssueSummaryCard label="New" count={summary.new} />
        <IssueSummaryCard label="Assigned" count={summary.assigned} />
        <IssueSummaryCard label="Resolved" count={summary.resolved} />
        <IssueSummaryCard label="Closed" count={summary.closed} />
      </div>

      <div className="dashboard-main-grid">
        <article className="my-task-panel">
          <div className="panel-title">내 할 일</div>

          {myTasks.length > 0 ? (
            <ul className="my-task-list">
              {myTasks.map((issue) => (
                <li key={issue.id} onClick={() => navigate(`/issues/${issue.id}`)}>
                  <strong>#{issue.id}</strong>
                  <span>{issue.title}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p className="dashboard-empty-message">할당된 이슈가 없습니다.</p>
          )}
        </article>

        <RecentIssueTable
          issues={recentIssues}
          onSelectIssue={(issueId) => navigate(`/issues/${issueId}`)}
        />
      </div>
    </section>
  );
}

export default DashboardPage;