export interface Project {
  id: number;
  name: string;
  description: string;
  createdAt?: string | null;
  issueCount?: number;
}

export interface ProjectCreateRequest {
  name: string;
  description: string;
}