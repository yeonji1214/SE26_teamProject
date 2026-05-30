import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getUsers, loginAsUser } from "../api/userApi";
import type { User } from "../types/user";
import { saveCurrentUser } from "../utils/authStorage";

function LoginPage() {
  const navigate = useNavigate();

  const [users, setUsers] = useState<User[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<number>(1);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);

  useEffect(() => {
    getUsers().then((data) => {
      setUsers(data);

      if (data.length > 0) {
        setSelectedUserId(data[0].id);
        setSelectedUser(data[0]);
      }
    });
  }, []);

  useEffect(() => {
    const user = users.find((item) => item.id === selectedUserId) ?? null;
    setSelectedUser(user);
  }, [selectedUserId, users]);

  const handleLogin = async () => {
    const user = await loginAsUser(selectedUserId);

    saveCurrentUser(user);
    navigate("/dashboard");
  };

  return (
    <section className="login-page">
      <div className="login-card">
        <div className="login-header">
          <h2>로그인</h2>
          <p>데모용 계정을 선택해서 로그인합니다.</p>
        </div>

        <label className="login-field">
          <span>계정</span>
          <select
            value={selectedUserId}
            onChange={(event) => setSelectedUserId(Number(event.target.value))}
          >
            {users.map((user) => (
              <option key={user.id} value={user.id}>
                {user.displayName} / {user.role}
              </option>
            ))}
          </select>
        </label>

        {selectedUser && (
          <div className="login-user-preview">
            <div className="login-avatar">
              {selectedUser.displayName.charAt(0)}
            </div>

            <div>
              <strong>{selectedUser.displayName}</strong>
              <p>
                @{selectedUser.username} · {selectedUser.role}
              </p>
            </div>
          </div>
        )}

        <button type="button" className="primary-button" onClick={handleLogin}>
          로그인
        </button>
      </div>
    </section>
  );
}

export default LoginPage;