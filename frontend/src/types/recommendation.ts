import type { User } from "./user";

export interface AssigneeRecommendation {
  assignee: User;
  score: number;
  matchedIssueCount: number;
  matchedTerms: string[];
  evidenceIssueTitles: string[];
  currentOpenAssignedIssueCount: number;
  explanation: string;
}