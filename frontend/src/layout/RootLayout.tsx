import { Outlet } from "react-router-dom";
import Header from "../components/Header";
import Sidebar from "../components/Sidebar";

function RootLayout() {
  return (
    <div className="app">
      <Header />

      <div className="app-body">
        <Sidebar />

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default RootLayout;