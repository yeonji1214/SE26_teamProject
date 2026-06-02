import { apiRequest } from "./apiClient";
import type { Project, ProjectCreateRequest } from "../types/project";

export async function getProjects(): Promise<Project[]> {
  return apiRequest<Project[]>("/api/projects");
}

export async function getProjectById(projectId: number): Promise<Project> {
  return apiRequest<Project>(`/api/projects/${projectId}`);
}

export async function createProject(
  request: ProjectCreateRequest
): Promise<Project> {
  return apiRequest<Project>("/api/projects", {
    method: "POST",
    body: request,
  });
}