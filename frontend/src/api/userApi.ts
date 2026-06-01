import type { User } from "../types/user";

const mockUsers: User[] = [
  {
    id: 1,
    username: "admin",
    displayName: "Admin",
    role: "ADMIN",
  },
  {
    id: 2,
    username: "pl1",
    displayName: "PL 1",
    role: "PL",
  },
  {
    id: 3,
    username: "pl2",
    displayName: "PL 2",
    role: "PL",
  },
  {
    id: 4,
    username: "dev1",
    displayName: "Developer 1",
    role: "DEV",
  },
  {
    id: 5,
    username: "dev2",
    displayName: "Developer 2",
    role: "DEV",
  },
  {
    id: 6,
    username: "tester1",
    displayName: "Tester 1",
    role: "TESTER",
  },
];

export async function getUsers(): Promise<User[]> {
  return mockUsers;
}

export async function getUserById(userId: number): Promise<User> {
  const user = mockUsers.find((item) => item.id === userId);

  if (!user) {
    throw new Error("사용자를 찾을 수 없습니다.");
  }

  return user;
}

export async function loginAsUser(userId: number): Promise<User> {
  return getUserById(userId);
}

export function getMockUsersForDev(): User[] {
  return mockUsers;
}