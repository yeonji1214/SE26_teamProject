import { NavLink } from "react-router-dom";

function Sidebar() {
  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
          Dashboard
        </NavLink>
        <NavLink to="/projects" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
          Projects
        </NavLink>

        <NavLink to="/issues" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
          Issues
        </NavLink>

        <NavLink to="/issues/new" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
          Create Issue
        </NavLink>

        <NavLink to="/statistics" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
          Statistics
        </NavLink>

        <NavLink to="/login" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>
          Login
        </NavLink>
      </nav>
    </aside>
  );
}

export default Sidebar;