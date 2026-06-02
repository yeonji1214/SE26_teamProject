import { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import type { User } from "../types/user";
import { AUTH_CHANGE_EVENT, clearCurrentUser, getCurrentUser } from "../utils/authStorage";

function Sidebar() {
  const navigate = useNavigate();
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  useEffect(() => {
    const syncCurrentUser = () => {
      setCurrentUser(getCurrentUser());
    };

    syncCurrentUser();

    window.addEventListener(AUTH_CHANGE_EVENT, syncCurrentUser);
    window.addEventListener("storage", syncCurrentUser);

    return () => {
      window.removeEventListener(AUTH_CHANGE_EVENT, syncCurrentUser);
      window.removeEventListener("storage", syncCurrentUser);
    };
  }, []);

  const handleLogout = () => {
    clearCurrentUser();
    setCurrentUser(null);
    navigate("/login");
  };

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/projects"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Projects
        </NavLink>

        <NavLink
          to="/issues"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Issues
        </NavLink>

        <NavLink
          to="/issues/new"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Create Issue
        </NavLink>

        <NavLink
          to="/statistics"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Statistics
        </NavLink>

        <NavLink
          to="/login"
          className={({ isActive }) =>
            isActive ? "sidebar-link active" : "sidebar-link"
          }
        >
          Login
        </NavLink>
      </nav>

      <div className="sidebar-user-box">
        {currentUser ? (
          <>
            <div className="sidebar-user-info">
              <div className="sidebar-user-avatar">
                {(currentUser.displayName || currentUser.username).charAt(0)}
              </div>

              <div>
                <strong>{currentUser.displayName}</strong>
                <span>{currentUser.role}</span>
              </div>
            </div>

            <button
              type="button"
              className="sidebar-logout-button"
              onClick={handleLogout}
            >
              로그아웃
            </button>
          </>
        ) : (
          <div className="sidebar-user-info">
            <div className="sidebar-user-avatar">?</div>

            <div>
              <strong>Guest</strong>
              <span>Not logged in</span>
            </div>
          </div>
        )}
      </div>
    </aside>
  );
}

export default Sidebar;