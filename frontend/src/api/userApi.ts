import { apiRequest } from "./apiClient";
import type { LoginRequest, PasswordLoginRequest, User, UserCreateRequest } from "../types/user";

export async function getUsers(): Promise<User[]> {
  return apiRequest<User[]>("/api/users");
}

export async function getUserById(userId: number): Promise<User> {
  return apiRequest<User>(`/api/users/${userId}`);
}

export async function createUser(request: UserCreateRequest): Promise<User> {
  return apiRequest<User>("/api/users", {
    method: "POST",
    body: request,
  });
}

export async function loginAsUser(userId: number): Promise<User> {
  const request: LoginRequest = { userId };

  return apiRequest<User>("/api/login", {
    method: "POST",
    body: request,
  });
}

export async function loginWithPassword(
  request: PasswordLoginRequest
): Promise<User> {
  return apiRequest<User>("/api/login/password", {
    method: "POST",
    body: request,
  });
}

/**
 * @deprecated
 * 기존 mock 기반 개발 코드와의 호환성을 위해 잠시 남겨두었소이다.
 * 새 UI 코드는 getUsers()를 사용해야 함.
 */
export function getMockUsersForDev(): User[] {
  return [];
}