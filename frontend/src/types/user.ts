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