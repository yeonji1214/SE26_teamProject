import { apiRequest, type QueryParams } from "./apiClient";
import type {
  Issue,
  IssueCreateRequest,
  IssueSearchCondition,
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
  const actorId = request.actorId ?? inferActorId();

  const normalized: IssueStatusUpdateRequest = {
    ...request,
    actorId,
  };

  if (request.status === "ASSIGNED") {
    const assigneeId = request.assigneeId ?? issue.assignee?.id;

    if (!assigneeId) {
      throw new Error("담당자를 선택하세요.");
    }

    normalized.assigneeId = assigneeId;
  }

  if (request.status === "FIXED") {
    normalized.fixerId = request.fixerId ?? actorId;
  }

  return normalized;
}

function inferActorId(): number {
  const currentUser = getCurrentUser();

  if (!currentUser) {
    throw new Error("로그인 정보가 없습니다. 다시 로그인해주세요.");
  }

  return currentUser.id;
}