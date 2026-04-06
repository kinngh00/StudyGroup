import { useCallback } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/atoms/Input";
import { Button } from "@/components/atoms/Button";
import { Spinner } from "@/components/atoms/Spinner";
import { GoogleIdTokenButton } from "@/components/molecules/GoogleIdTokenButton";
import { useGoogleLoginMutation, useSignupMutation } from "@/api/baseApi";
import { useToast } from "@/components/organisms/ToastProvider";
import { useAppDispatch } from "@/app/hooks";
import { setCredentials } from "@/features/auth/authSlice";

type ApiErrorShape = {
  message?: string;
  data?: { message?: string };
};

const normalizeEmail = (value: string) => value.normalize("NFKC").replace(/[\u200B-\u200D\uFEFF]/g, "").trim();

const signupSchema = z
  .object({
    name: z.string({ required_error: "이름은 필수입니다." }).min(1, "이름은 필수입니다.").min(2, "이름은 2자 이상이어야 합니다."),
    email: z.preprocess(
      (value) => (typeof value === "string" ? normalizeEmail(value) : value),
      z.string({ required_error: "이메일은 필수입니다." }).min(1, "이메일은 필수입니다.").email("유효한 이메일을 입력해 주세요.")
    ),
    password: z.string({ required_error: "비밀번호는 필수입니다." }).min(1, "비밀번호는 필수입니다.").min(8, "비밀번호는 8자 이상이어야 합니다."),
    passwordConfirm: z.string({ required_error: "비밀번호 확인은 필수입니다." }).min(1, "비밀번호 확인은 필수입니다.")
  })
  .refine((value) => value.password === value.passwordConfirm, {
    path: ["passwordConfirm"],
    message: "비밀번호가 일치하지 않습니다."
  });

type SignupValues = z.infer<typeof signupSchema>;

export const SignupPage = () => {
  const navigate = useNavigate();
  const { notify } = useToast();
  const dispatch = useAppDispatch();
  const [signup, { isLoading }] = useSignupMutation();
  const [googleLogin, { isLoading: googleLoading }] = useGoogleLoginMutation();

  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<SignupValues>({ resolver: zodResolver(signupSchema) });

  const onSubmit = async ({ name, email, password }: SignupValues) => {
    try {
      await signup({ name, email, password }).unwrap();
      notify("success", "회원가입 완료", "이제 로그인할 수 있습니다.");
      navigate("/login");
    } catch (error) {
      const err = error as ApiErrorShape;
      const detail = err?.data?.message ?? err?.message ?? "잠시 후 다시 시도해 주세요.";
      notify("error", "회원가입 실패", detail);
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
        const err = error as ApiErrorShape;
        const detail = err?.data?.message ?? err?.message ?? "Google 로그인에 실패했습니다.";
        notify("error", "구글 로그인 실패", detail);
      }
    },
    [dispatch, googleLoading, googleLogin, navigate, notify]
  );

  return (
    <div className="mx-auto max-w-md pt-8 space-y-3">
      <div className="panel p-7">
        <h1 className="mb-1 text-2xl font-bold text-slate-900">회원가입</h1>
        <p className="mb-4 text-sm text-slate-600">지금 바로 첫 스터디 그룹을 시작해 보세요.</p>
        <form className="space-y-3" onSubmit={handleSubmit(onSubmit)}>
          <Input placeholder="이름" error={errors.name?.message} {...register("name")} />
          <Input placeholder="이메일" error={errors.email?.message} {...register("email")} />
          <Input type="password" placeholder="비밀번호" error={errors.password?.message} {...register("password")} />
          <Input type="password" placeholder="비밀번호 확인" error={errors.passwordConfirm?.message} {...register("passwordConfirm")} />
          <Button className="w-full" disabled={isLoading} type="submit">
            {isLoading ? (
              <>
                <Spinner />
                가입 중...
              </>
            ) : (
              "회원가입"
            )}
          </Button>
        </form>
      </div>
      <div className="panel p-5">
        <p className="mb-3 text-xs text-slate-500">또는 Google 계정으로 시작</p>
        {googleLoading ? <p className="text-sm text-slate-500">구글 로그인 처리 중...</p> : null}
        <GoogleIdTokenButton buttonText="signup_with" onSuccess={handleGoogleLogin} />
      </div>
    </div>
  );
};
