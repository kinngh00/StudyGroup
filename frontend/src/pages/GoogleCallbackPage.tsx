import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAppDispatch } from "@/app/hooks";
import { setCredentials } from "@/features/auth/authSlice";

export const GoogleCallbackPage = () => {
  const [params] = useSearchParams();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const token = params.get("token");
    const userId = Number(params.get("id") ?? 0);
    const name = params.get("name") ?? "구글 사용자";
    const email = params.get("email") ?? "google-user@example.com";

    if (!token) {
      navigate("/login");
      return;
    }

    dispatch(
      setCredentials({
        token,
        user: { id: userId, name, email }
      })
    );
    navigate("/dashboard");
  }, [dispatch, navigate, params]);

  return <p className="panel p-4 text-sm text-slate-700">구글 로그인 처리 중입니다...</p>;
};
