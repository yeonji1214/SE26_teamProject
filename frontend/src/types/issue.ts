import type { User } from "./user";

export type IssuePriority =
  | "BLOCKER"
  | "CRITICAL"
  | "MAJOR"
  | "MINOR"
  | "TRIVIAL";

export type IssueStatus =
  | "NEW"
  | "ASSIGNED"
  | "FIXED"
  | "RESOLVED"
  | "CLOSED"
  | "REOPENED";

export interface IssueComment {
  id: number;
  issueId: number;
  author: User;
  content: string;
  createdAt: string;
}

export interface Issue {
  id: number;
  projectId: number;
  title: string;
  description: string;
  reporter: User;
  assignee: User | null;
  fixer: User | null;
  priority: IssuePriority;
  status: IssueStatus;
  reportedDate: string;
  comments: IssueComment[];
}

export interface IssueCreateRequest {
  projectId: number;
  title: string;
  description: string;
  priority: IssuePriority;
  reporterId: number;
}

export interface IssueStatusUpdateRequest {
  status: IssueStatus;
  actorId?: number;
  assigneeId?: number;
  fixerId?: number;
  comment?: string;
}

export interface IssueSearchCondition {
  keyword?: string;
  status?: IssueStatus | "ALL";
  assigneeId?: number | "ALL";
  reporterId?: number | "ALL";
}