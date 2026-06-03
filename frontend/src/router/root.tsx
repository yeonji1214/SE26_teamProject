import { createBrowserRouter, Navigate } from "react-router-dom";
import RootLayout from "../layout/RootLayout";
import LoginPage from "../pages/LoginPage";
import ProjectPage from "../pages/ProjectPage";
import IssueListPage from "../pages/IssueListPage";
import IssueCreatePage from "../pages/IssueCreatePage";
import IssueDetailPage from "../pages/IssueDetailPage";
import StatisticsPage from "../pages/StatisticsPage";
import DashboardPage from "../pages/DashboardPage";
import UserManagementPage from "../pages/UserManagementPage";
import ProtectedRoute from "./ProtectedRoute";

const root = createBrowserRouter([
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: "/",
        element: <RootLayout />,
        children: [
          {
            index: true,
            element: <Navigate to="/dashboard" replace />,
          },
          {
            path: "dashboard",
            element: <DashboardPage />,
          },
          {
            path: "dashboard/:projectId",
            element: <DashboardPage />,
          },
          {
            path: "projects",
            element: <ProjectPage />,
          },
          {
            path: "issues",
            element: <IssueListPage />,
          },
          {
            path: "issues/new",
            element: <IssueCreatePage />,
          },
          {
            path: "issues/:issueId",
            element: <IssueDetailPage />,
          },
          {
            path: "statistics",
            element: <StatisticsPage />,
          },
          {
            path: "/users",
            element: <UserManagementPage />,
          },
        ],
      },
    ],
  },
  {
    path: "*",
    element: <Navigate to="/dashboard" replace />,
  },
]);

export default root;