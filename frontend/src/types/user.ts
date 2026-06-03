export type AccountRole = "ADMIN" | "PL" | "DEV" | "TESTER";

export interface User {
  id: number;
  username: string;
  displayName: string;
  role: AccountRole;
}

export interface LoginRequest {
  userId: number;
}

export interface PasswordLoginRequest {
  username: string;
  password: string;
}

export interface UserCreateRequest {
  username: string;
  password: string;
  role: AccountRole;
}