import { apiRequest } from "./apiClient";
import type { AssigneeRecommendation } from "../types/recommendation";

export async function getAssigneeRecommendations(
  issueId: number,
  limit = 3
): Promise<AssigneeRecommendation[]> {
  return apiRequest<AssigneeRecommendation[]>(
    `/api/issues/${issueId}/recommendations`,
    {
      query: { limit },
    }
  );
}