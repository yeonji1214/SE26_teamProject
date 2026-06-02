import { apiRequest } from "./apiClient";
import type { StatisticsResponse } from "../types/statistics";

export async function getStatistics(): Promise<StatisticsResponse> {
  return apiRequest<StatisticsResponse>("/api/statistics");
}