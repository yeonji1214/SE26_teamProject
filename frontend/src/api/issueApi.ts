import type {
  Issue,
  IssueCreateRequest,
  IssueSearchCondition,
  IssueStatusUpdateRequest,
} from "../types/issue";
import type { User } from "../types/user";

const admin: User = {
  id: 1,
  username: "admin",
  displayName: "Admin",
  role: "ADMIN",
};

const pl1: User = {
  id: 2,
  username: "pl1",
  displayName: "PL 1",
  role: "PL",
};

const dev1: User = {
  id: 4,
  username: "dev1",
  displayName: "Developer 1",
  role: "DEV",
};

const dev2: User = {
  id: 5,
  username: "dev2",
  displayName: "Developer 2",
  role: "DEV",
};

const tester1: User = {
  id: 6,
  username: "tester1",
  displayName: "Tester 1",
  role: "TESTER",
};

const mockUsers: User[] = [admin, pl1, dev1, dev2, tester1];

let mockIssues: Issue[] = [
  {
    id: 1,
    projectId: 1,
    title: "로그인 후 이슈 목록이 보이지 않음",
    description:
      "tester1 계정으로 로그인한 뒤 이슈 목록 화면에 진입하면 빈 화면만 표시됩니다.",
    reporter: tester1,
    assignee: dev1,
    fixer: null,
    priority: "MAJOR",
    status: "ASSIGNED",
    reportedDate: "2026-05-22T10:00:00",
    comments: [
      {
        id: 1,
        issueId: 1,
        author: tester1,
        content: "로그인 직후 이슈 목록이 렌더링되지 않습니다.",
        createdAt: "2026-05-22T10:05:00",
      },
      {
        id: 2,
        issueId: 1,
        author: pl1,
        content: "dev1에게 배정합니다.",
        createdAt: "2026-05-22T10:20:00",
      },
    ],
  },
  {
    id: 2,
    projectId: 1,
    title: "이슈 등록 시 priority 기본값 확인 필요",
    description:
      "priority를 따로 선택하지 않았을 때 기본값이 major로 저장되는지 확인해야 합니다.",
    reporter: tester1,
    assignee: null,
    fixer: null,
    priority: "MAJOR",
    status: "NEW",
    reportedDate: "2026-05-22T11:00:00",
    comments: [],
  },
  {
    id: 3,
    projectId: 1,
    title: "통계 페이지 월별 이슈 수 표시 오류",
    description:
      "월별 통계에서 resolved 상태의 이슈가 전체 개수에 반영되지 않는 문제가 있습니다.",
    reporter: tester1,
    assignee: dev2,
    fixer: dev2,
    priority: "MINOR",
    status: "RESOLVED",
    reportedDate: "2026-05-21T16:30:00",
    comments: [
      {
        id: 3,
        issueId: 3,
        author: dev2,
        content: "집계 조건을 수정했고 tester 확인을 요청합니다.",
        createdAt: "2026-05-21T18:00:00",
      },
    ],
  },
];

function findUserById(userId: number): User {
  const user = mockUsers.find((item) => item.id === userId);

  if (!user) {
    throw new Error("사용자를 찾을 수 없습니다.");
  }

  return user;
}

export async function getIssues(): Promise<Issue[]> {
  return mockIssues;
}

export async function searchIssues(
  condition: IssueSearchCondition
): Promise<Issue[]> {
  return mockIssues.filter((issue) => {
    const keyword = condition.keyword?.trim().toLowerCase();

    const matchesKeyword =
      !keyword ||
      issue.title.toLowerCase().includes(keyword) ||
      issue.description.toLowerCase().includes(keyword);

    const matchesStatus =
      !condition.status ||
      condition.status === "ALL" ||
      issue.status === condition.status;

    const matchesAssignee =
      !condition.assigneeId ||
      condition.assigneeId === "ALL" ||
      issue.assignee?.id === condition.assigneeId;

    const matchesReporter =
      !condition.reporterId ||
      condition.reporterId === "ALL" ||
      issue.reporter.id === condition.reporterId;

    return (
      matchesKeyword &&
      matchesStatus &&
      matchesAssignee &&
      matchesReporter
    );
  });
}

export async function getIssueById(issueId: number): Promise<Issue> {
  const issue = mockIssues.find((item) => item.id === issueId);

  if (!issue) {
    throw new Error("이슈를 찾을 수 없습니다.");
  }

  return issue;
}

export async function createIssue(
  request: IssueCreateRequest
): Promise<Issue> {
  const reporter = findUserById(request.reporterId);

  const newIssue: Issue = {
    id: Date.now(),
    projectId: request.projectId,
    title: request.title,
    description: request.description,
    reporter,
    assignee: null,
    fixer: null,
    priority: request.priority,
    status: "NEW",
    reportedDate: new Date().toISOString(),
    comments: [],
  };

  mockIssues = [newIssue, ...mockIssues];

  return newIssue;
}

export async function updateIssueStatus(
  issueId: number,
  request: IssueStatusUpdateRequest
): Promise<Issue> {
  const targetIssue = await getIssueById(issueId);

  const assignee =
    request.assigneeId !== undefined
      ? findUserById(request.assigneeId)
      : targetIssue.assignee;

  const fixer =
    request.fixerId !== undefined
      ? findUserById(request.fixerId)
      : targetIssue.fixer;

  const nextComments = request.comment?.trim()
    ? [
        ...targetIssue.comments,
        {
          id: Date.now(),
          issueId,
          author: admin,
          content: request.comment.trim(),
          createdAt: new Date().toISOString(),
        },
      ]
    : targetIssue.comments;

  const updatedIssue: Issue = {
    ...targetIssue,
    status: request.status,
    assignee,
    fixer,
    comments: nextComments,
  };

  mockIssues = mockIssues.map((issue) =>
    issue.id === issueId ? updatedIssue : issue
  );

  return updatedIssue;
}

export async function addIssueComment(
  issueId: number,
  authorId: number,
  content: string
): Promise<Issue> {
  const targetIssue = await getIssueById(issueId);
  const author = findUserById(authorId);

  const updatedIssue: Issue = {
    ...targetIssue,
    comments: [
      ...targetIssue.comments,
      {
        id: Date.now(),
        issueId,
        author,
        content,
        createdAt: new Date().toISOString(),
      },
    ],
  };

  mockIssues = mockIssues.map((issue) =>
    issue.id === issueId ? updatedIssue : issue
  );

  return updatedIssue;
}