export interface StatisticsResponse {
  totalIssues: number;
  byStatus: Record<string, number>;
  byPriority: Record<string, number>;
  byDay: Record<string, number>;
  byMonth: Record<string, number>;
  byAssignee: Record<string, number>;
}