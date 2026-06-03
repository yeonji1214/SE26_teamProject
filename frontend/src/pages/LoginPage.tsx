import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginWithPassword } from "../api/userApi";
import { saveCurrentUser } from "../utils/authStorage";

function LoginPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleLogin = async () => {
    if (!username.trim() || !password.trim()) {
      setErrorMessage("아이디와 비밀번호를 모두 입력하세요.");
      return;
    }

    setIsLoggingIn(true);
    setErrorMessage("");

    try {
      const user = await loginWithPassword({
        username: username.trim(),
        password,
      });

      saveCurrentUser(user);
      navigate("/dashboard");
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "로그인에 실패했습니다."
      );
    } finally {
      setIsLoggingIn(false);
    }
  };

  return (
    <section className="login-page">
      <div className="login-card">
        <div className="login-header">
          <h2>로그인</h2>
        </div>

        <label className="login-field">
          <span>아이디</span>
          <input
            type="text"
            value={username}
            autoComplete="username"
            onChange={(event) => setUsername(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                handleLogin();
              }
            }}
          />
        </label>

        <label className="login-field">
          <span>비밀번호</span>
          <input
            type="password"
            value={password}
            autoComplete="current-password"
            onChange={(event) => setPassword(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                handleLogin();
              }
            }}
          />
        </label>

        {errorMessage && <p className="error-message">{errorMessage}</p>}

        <button
          type="button"
          className="primary-button"
          onClick={handleLogin}
          disabled={isLoggingIn}
        >
          {isLoggingIn ? "로그인 중..." : "로그인"}
        </button>
      </div>
    </section>
  );
}

export default LoginPage;