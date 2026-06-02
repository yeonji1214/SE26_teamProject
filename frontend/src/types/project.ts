export interface Project {
  id: number;
  name: string;
  description: string;
  createdAt?: string;
}

export interface ProjectCreateRequest {
  name: string;
  description: string;
}