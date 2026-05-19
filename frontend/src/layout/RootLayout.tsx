import { NavLink, Outlet } from "react-router-dom";
import Header from "../components/Header";

function RootLayout() {
  return (
    <div className="app">
      <Header />

      <div className="app-body">
        <aside className="sidebar">
          <NavLink to="/projects">Projects</NavLink>
          <NavLink to="/issues">Issues</NavLink>
          <NavLink to="/issues/new">Create Issue</NavLink>
          <NavLink to="/statistics">Statistics</NavLink>
          <NavLink to="/login">Login</NavLink>
        </aside>

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default RootLayout;