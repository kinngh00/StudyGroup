import { useNavigate } from "react-router-dom";
import { LoginForm, type LoginFormValues } from "@/components/molecules/LoginForm";
import { useLoginMutation } from "@/api/baseApi";
import { useAppDispatch } from "@/app/hooks";
import { setCredentials } from "@/features/auth/authSlice";
import { useToast } from "@/components/organisms/ToastProvider";

export const LoginPage = () => {
  const [login, { isLoading }] = useLoginMutation();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { notify } = useToast();

  const handleSubmit = async (values: LoginFormValues) => {
    try {
      const response = await login(values).unwrap();
      dispatch(setCredentials({ token: response.token, user: response.user }));
      notify("success", "로그인 성공");
      navigate("/dashboard");
    } catch {
      notify("error", "로그인 실패", "이메일과 비밀번호를 확인해 주세요.");
    }
  };

  return (
    <div className="mx-auto max-w-md pt-8">
      <LoginForm onSubmit={handleSubmit} loading={isLoading} />
    </div>
  );
};
