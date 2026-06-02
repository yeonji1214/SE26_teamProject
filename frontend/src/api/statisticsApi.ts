import { apiRequest } from "./apiClient";
import { getIssues } from "./issueApi";
import type { StatisticsResponse } from "../types/statistics";

export async function getStatistics(): Promise<StatisticsResponse> {
  return apiRequest<StatisticsResponse>("/api/statistics");
}

type IssueLike = {
  projectId?: number | string;
  project?: {
    id?: number | string;
  };
  status?: string;
  priority?: string;
  assignee?:
    | string
    | null
    | {
        username?: string;
        displayName?: string;
      };
  reportedDate?: string;
  createdAt?: string;
};

const ISSUE_STATUSES = [
  "NEW",
  "ASSIGNED",
  "FIXED",
  "RESOLVED",
  "CLOSED",
  "REOPENED",
] as const;

export async function getStatisticsByProjectFromIssues(
  projectId: number
): Promise<StatisticsResponse> {
  const issues = (await getIssues()) as IssueLike[];

  const projectIssues = issues.filter((issue) => {
    const issueProjectId = issue.projectId ?? issue.project?.id;
    return Number(issueProjectId) === projectId;
  });

  const byStatus: Record<string, number> = {
    NEW: 0,
    ASSIGNED: 0,
    FIXED: 0,
    RESOLVED: 0,
    CLOSED: 0,
    REOPENED: 0,
  };

  const byPriority: Record<string, number> = {
    BLOCKER: 0,
    CRITICAL: 0,
    MAJOR: 0,
    MINOR: 0,
    TRIVIAL: 0,
  };

  const byAssignee: Record<string, number> = {};
  const byMonth: Record<string, number> = {};
  const byDay: Record<string, number> = {};

  projectIssues.forEach((issue) => {
    const status = issue.status;

    if (status && ISSUE_STATUSES.includes(status as any)) {
      byStatus[status] = (byStatus[status] ?? 0) + 1;
    }

    const priority = issue.priority ?? "MAJOR";
    byPriority[priority] = (byPriority[priority] ?? 0) + 1;

    const assigneeName = getAssigneeName(issue.assignee);
    if (assigneeName) {
      byAssignee[assigneeName] = (byAssignee[assigneeName] ?? 0) + 1;
    }

    const dateText = issue.reportedDate ?? issue.createdAt;

    const monthKey = getMonthKey(dateText);
    if (monthKey) {
      byMonth[monthKey] = (byMonth[monthKey] ?? 0) + 1;
    }

    const dayKey = getDayKey(dateText);
    if (dayKey) {
      byDay[dayKey] = (byDay[dayKey] ?? 0) + 1;
    }
  });

  return {
    totalIssues: projectIssues.length,
    byStatus,
    byPriority,
    byAssignee,
    byMonth,
    byDay,
  };
}

function getAssigneeName(assignee: IssueLike["assignee"]) {
  if (!assignee) {
    return null;
  }

  if (typeof assignee === "string") {
    return assignee;
  }

  return assignee.displayName ?? assignee.username ?? null;
}

function getMonthKey(dateText?: string) {
  if (!dateText) {
    return null;
  }

  const date = new Date(dateText);

  if (Number.isNaN(date.getTime())) {
    return null;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");

  return `${year}-${month}`;
}

function getDayKey(dateText?: string) {
  if (!dateText) {
    return null;
  }

  const date = new Date(dateText);

  if (Number.isNaN(date.getTime())) {
    return null;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
}