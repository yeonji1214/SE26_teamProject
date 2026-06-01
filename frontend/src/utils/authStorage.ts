import type { User } from "../types/user";

const CURRENT_USER_KEY = "issue-tracker-current-user";

export function saveCurrentUser(user: User): void {
  localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(user));
}

export function getCurrentUser(): User | null {
  const storedUser = localStorage.getItem(CURRENT_USER_KEY);

  if (!storedUser) {
    return null;
  }

  try {
    return JSON.parse(storedUser) as User;
  } catch {
    localStorage.removeItem(CURRENT_USER_KEY);
    return null;
  }
}

export function clearCurrentUser(): void {
  localStorage.removeItem(CURRENT_USER_KEY);
}