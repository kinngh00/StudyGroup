import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { LoginForm, type LoginFormValues } from "@/components/molecules/LoginForm";
import { GoogleIdTokenButton } from "@/components/molecules/GoogleIdTokenButton";
import { useGoogleLoginMutation, useLoginMutation } from "@/api/baseApi";
import { useAppDispatch } from "@/app/hooks";
import { setCredentials } from "@/features/auth/authSlice";
import { useToast } from "@/components/organisms/ToastProvider";

export const LoginPage = () => {
  const [login, { isLoading }] = useLoginMutation();
  const [googleLogin, { isLoading: googleLoading }] = useGoogleLoginMutation();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { notify } = useToast();

  const handleSubmit = async (loginFormValues: LoginFormValues) => {
    try {
      const response = await login(loginFormValues).unwrap();
      dispatch(
        setCredentials({
          token: response.accessToken,
          refreshToken: response.refreshToken,
          user: { id: 0, name: response.name, email: response.email }
        })
      );
      notify("success", "로그인 성공");
      navigate("/dashboard");
    } catch (error) {
      const err = error as { message?: string; data?: { message?: string } };
      const detail = err?.data?.message ?? err?.message ?? "이메일과 비밀번호를 확인해 주세요.";
      notify("error", "로그인 실패", detail);
    }
  };

  const handleGoogleLogin = useCallback(
    async (idToken: string) => {
      if (googleLoading) {
        return;
      }

      try {
        const response = await googleLogin({ idToken }).unwrap();
        dispatch(
          setCredentials({
            token: response.accessToken,
            refreshToken: response.refreshToken,
            user: { id: 0, name: response.name, email: response.email }
          })
        );
        notify("success", "구글 로그인 성공");
        navigate("/dashboard");
      } catch (error) {
        const err = error as { message?: string; data?: { message?: string } };
        const detail = err?.data?.message ?? err?.message ?? "Google 로그인에 실패했습니다.";
        notify("error", "구글 로그인 실패", detail);
      }
    },
    [dispatch, googleLoading, googleLogin, navigate, notify]
  );

  return (
    <div className="mx-auto max-w-md pt-8 space-y-3">
      <LoginForm loading={isLoading} onSubmit={handleSubmit} />
      <div className="panel p-5">
        <p className="mb-3 text-xs text-slate-500">또는 Google 계정으로 로그인</p>
        <GoogleIdTokenButton buttonText="signin_with" onSuccess={handleGoogleLogin} />
      </div>
    </div>
  );
};
