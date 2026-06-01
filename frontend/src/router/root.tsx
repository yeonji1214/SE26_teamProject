import { createBrowserRouter } from "react-router-dom";
import RootLayout from "../layout/RootLayout";
import LoginPage from "../pages/LoginPage";
import ProjectPage from "../pages/ProjectPage";
import IssueListPage from "../pages/IssueListPage";
import IssueCreatePage from "../pages/IssueCreatePage";
import IssueDetailPage from "../pages/IssueDetailPage";
import StatisticsPage from "../pages/StatisticsPage";
import DashboardPage from "../pages/DashboardPage";

const root = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      {
        index: true,
        element: <LoginPage />,
      },
      {
        path: "dashboard",
        element: <DashboardPage />,
      },
      {
        path: "login",
        element: <LoginPage />,
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
    ],
  },
]);

export default root;