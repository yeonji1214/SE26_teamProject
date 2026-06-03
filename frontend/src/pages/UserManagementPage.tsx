import { useEffect, useState } from "react";
import { createUser, getUsers } from "../api/userApi";
import type { AccountRole, User } from "../types/user";
import { getCurrentUser } from "../utils/authStorage";

const ROLE_OPTIONS: AccountRole[] = ["ADMIN", "PL", "DEV", "TESTER"];

function UserManagementPage() {
  const currentUser = getCurrentUser();

  const [users, setUsers] = useState<User[]>([]);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<AccountRole>("TESTER");
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const isAdmin = currentUser?.role === "ADMIN";

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const data = await getUsers();
      setUsers(data);
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "사용자 목록을 불러오지 못했습니다."
      );
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateUser = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!isAdmin) {
      setErrorMessage("ADMIN 계정만 사용자를 추가할 수 있습니다.");
      return;
    }

    if (!username.trim() || !password.trim()) {
      setErrorMessage("아이디와 비밀번호를 모두 입력하세요.");
      return;
    }

    setIsSubmitting(true);
    setErrorMessage("");
    setSuccessMessage("");

    try {
      await createUser({
        username: username.trim(),
        password,
        role,
      });

      setUsername("");
      setPassword("");
      setRole("TESTER");
      setSuccessMessage("사용자가 추가되었습니다.");
      await loadUsers();
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "사용자 추가에 실패했습니다."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <section className="users-page">
      <div className="users-page-header">
        <div>
          <p className="eyebrow">Account Management</p>
          <h2>Users</h2>
          <p>
            사용자 계정을 조회하고 ADMIN 계정으로 새 사용자를 추가합니다.
          </p>
        </div>

        <div className="users-summary-card">
          <span>총 사용자</span>
          <strong>{users.length}</strong>
        </div>
      </div>

      {!isAdmin && (
        <article className="notice-card">
          <div>
            <strong>권한 안내</strong>
            <p>사용자 추가는 ADMIN 계정으로 로그인했을 때만 가능합니다.</p>
          </div>
        </article>
      )}

      {isAdmin && (
        <article className="user-card">
          <div className="card-title-row">
            <div>
              <h3>사용자 추가</h3>
              <p>새 계정의 아이디, 비밀번호, 역할을 입력하세요.</p>
            </div>
          </div>

          <form className="user-create-form" onSubmit={handleCreateUser}>
            <label className="user-form-field">
              <span>아이디</span>
              <input
                type="text"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
              />
            </label>

            <label className="user-form-field">
              <span>비밀번호</span>
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </label>

            <label className="user-form-field">
              <span>역할</span>
              <select
                value={role}
                onChange={(event) => setRole(event.target.value as AccountRole)}
              >
                {ROLE_OPTIONS.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </label>

            <button
              type="submit"
              className="user-create-button"
              disabled={isSubmitting}
            >
              {isSubmitting ? "추가 중..." : "사용자 추가"}
            </button>
          </form>

          {errorMessage && <p className="error-message">{errorMessage}</p>}
          {successMessage && <p className="success-message">{successMessage}</p>}
        </article>
      )}

      <article className="user-card">
        <div className="card-title-row">
          <div>
            <h3>사용자 목록</h3>
            <p>등록된 사용자와 역할 정보를 확인합니다.</p>
          </div>
        </div>

        {isLoading && <p className="empty-state">사용자 목록을 불러오는 중입니다.</p>}

        {!isLoading && users.length === 0 && (
          <p className="empty-state">등록된 사용자가 없습니다.</p>
        )}

        {!isLoading && users.length > 0 && (
          <div className="user-table-wrapper">
            <table className="user-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>아이디</th>
                  <th>역할</th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.id}</td>
                    <td>
                      <div className="user-name-cell">
                        <div className="user-avatar">
                          {user.username.charAt(0).toUpperCase()}
                        </div>
                        <span>{user.username}</span>
                      </div>
                    </td>
                    <td>
                      <span className={`role-badge role-${user.role.toLowerCase()}`}>
                        {user.role}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </article>
    </section>
  );
}

export default UserManagementPage;