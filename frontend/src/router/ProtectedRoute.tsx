import { Navigate, Outlet } from "react-router-dom";
import { getCurrentUser } from "../utils/authStorage";

function ProtectedRoute() {
  const currentUser = getCurrentUser();

  if (!currentUser) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;