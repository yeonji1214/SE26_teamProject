import { apiRequest, type QueryParams } from "./apiClient";
import type {
  Issue,
  IssueCreateRequest,
  IssueSearchCondition,
  IssueStatus,
  IssueStatusUpdateRequest,
} from "../types/issue";
import { getCurrentUser } from "../utils/authStorage";

export async function getIssues(): Promise<Issue[]> {
  return apiRequest<Issue[]>("/api/issues");
}

export async function getIssuesByProjectId(projectId: number): Promise<Issue[]> {
  const issues = await getIssues();

  return issues.filter((issue) => {
    const issueWithProject = issue as Issue & {
      projectId?: number;
      project?: {
        id: number;
      };
    };

    return (
      issueWithProject.projectId === projectId ||
      issueWithProject.project?.id === projectId
    );
  });
}

export async function searchIssues(
  condition: IssueSearchCondition
): Promise<Issue[]> {
  return apiRequest<Issue[]>("/api/issues", {
    query: toIssueSearchQuery(condition),
  });
}

export async function getIssueById(issueId: number): Promise<Issue> {
  return apiRequest<Issue>(`/api/issues/${issueId}`);
}

export async function createIssue(
  request: IssueCreateRequest
): Promise<Issue> {
  return apiRequest<Issue>("/api/issues", {
    method: "POST",
    body: request,
  });
}

export async function updateIssueStatus(
  issueId: number,
  request: IssueStatusUpdateRequest
): Promise<Issue> {
  const currentIssue = await getIssueById(issueId);
  const normalizedRequest = normalizeStatusUpdateRequest(currentIssue, request);

  return apiRequest<Issue>(`/api/issues/${issueId}/status`, {
    method: "PATCH",
    body: normalizedRequest,
  });
}

export async function addIssueComment(
  issueId: number,
  authorId: number,
  content: string
): Promise<Issue> {
  await apiRequest(`/api/issues/${issueId}/comments`, {
    method: "POST",
    body: {
      authorId,
      content,
    },
  });

  return getIssueById(issueId);
}

function toIssueSearchQuery(condition: IssueSearchCondition): QueryParams {
  return {
    keyword: condition.keyword,
    status:
      condition.status === undefined || condition.status === "ALL"
        ? undefined
        : condition.status,
    assigneeId:
      condition.assigneeId === undefined || condition.assigneeId === "ALL"
        ? undefined
        : condition.assigneeId,
    reporterId:
      condition.reporterId === undefined || condition.reporterId === "ALL"
        ? undefined
        : condition.reporterId,
  };
}

function normalizeStatusUpdateRequest(
  issue: Issue,
  request: IssueStatusUpdateRequest
): IssueStatusUpdateRequest {
  const actorId = request.actorId ?? inferActorId(issue, request.status);
  const normalized: IssueStatusUpdateRequest = {
    ...request,
    actorId,
  };

  if (request.status === "ASSIGNED") {
    normalized.assigneeId =
      request.assigneeId ?? issue.assignee?.id ?? defaultDeveloperId();
  }

  if (request.status === "FIXED") {
    normalized.fixerId =
      request.fixerId ?? request.actorId ?? issue.assignee?.id ?? actorId;
  }

  return normalized;
}

function inferActorId(issue: Issue, status: IssueStatus): number {
  const currentUser = getCurrentUser();

  if (currentUser) {
    return currentUser.id;
  }

  if (status === "FIXED") {
    return issue.assignee?.id ?? defaultDeveloperId();
  }

  if (status === "RESOLVED") {
    return issue.reporter.id;
  }

  return defaultProjectLeaderId();
}

function defaultProjectLeaderId(): number {
  return 2;
}

function defaultDeveloperId(): number {
  return 4;
}