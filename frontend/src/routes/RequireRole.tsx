import { Navigate, Outlet } from "react-router-dom";
import type { UserRole } from "@/types/domain";
import { useAppSelector } from "@/app/hooks";

interface RequireRoleProps {
  allowedRoles: UserRole[];
}

export const RequireRole = ({ allowedRoles }: RequireRoleProps) => {
  const role = useAppSelector((state) => state.study.userRoleInCurrentStudy);
  if (!allowedRoles.includes(role)) {
    return <Navigate replace to="/dashboard" />;
  }
  return <Outlet />;
};
