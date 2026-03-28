import { createBrowserRouter } from "react-router-dom";
import { MainLayout } from "@/layouts/MainLayout";
import { HomePage } from "@/pages/HomePage";
import { LoginPage } from "@/pages/LoginPage";
import { SignupPage } from "@/pages/SignupPage";
import { StudyDetailPage } from "@/pages/StudyDetailPage";
import { DashboardPage } from "@/pages/DashboardPage";
import { StudyCreatePage } from "@/pages/StudyCreatePage";
import { RecruitmentCreatePage } from "@/pages/RecruitmentCreatePage";
import { StudyManagePage } from "@/pages/StudyManagePage";
import { StudyInsidePage } from "@/pages/StudyInsidePage";
import { GoogleCallbackPage } from "@/pages/GoogleCallbackPage";
import { NotFoundPage } from "@/pages/NotFoundPage";
import { RequireAuth } from "@/routes/RequireAuth";
import { RequireRole } from "@/routes/RequireRole";
import { StudyRoleGate } from "@/routes/StudyRoleGate";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      { path: "login", element: <LoginPage /> },
      { path: "signup", element: <SignupPage /> },
      { path: "auth/google/callback", element: <GoogleCallbackPage /> },
      { path: "study/:studyId", element: <StudyDetailPage /> },
      {
        element: <RequireAuth />,
        children: [
          { path: "dashboard", element: <DashboardPage /> },
          { path: "study/create", element: <StudyCreatePage /> },
          {
            path: "study/:studyId",
            element: <StudyRoleGate />,
            children: [
              { path: "inside", element: <StudyInsidePage /> },
              {
                element: <RequireRole allowedRoles={["Owner", "Admin"]} />,
                children: [
                  { path: "recruitment/create", element: <RecruitmentCreatePage /> },
                  { path: "manage", element: <StudyManagePage /> }
                ]
              }
            ]
          }
        ]
      },
      { path: "*", element: <NotFoundPage /> }
    ]
  }
]);
