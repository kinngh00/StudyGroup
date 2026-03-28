import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/atoms/Input";
import { Button } from "@/components/atoms/Button";
import { Spinner } from "@/components/atoms/Spinner";
import { useSignupMutation } from "@/api/baseApi";
import { useToast } from "@/components/organisms/ToastProvider";

const signupSchema = z
  .object({
    name: z.string().min(2, "이름은 2자 이상이어야 합니다."),
    email: z.string().email("유효한 이메일을 입력해 주세요."),
    password: z.string().min(8, "비밀번호는 8자 이상이어야 합니다."),
    passwordConfirm: z.string()
  })
  .refine((value) => value.password === value.passwordConfirm, {
    path: ["passwordConfirm"],
    message: "비밀번호가 일치하지 않습니다."
  });

type SignupValues = z.infer<typeof signupSchema>;

export const SignupPage = () => {
  const navigate = useNavigate();
  const { notify } = useToast();
  const [signup, { isLoading }] = useSignupMutation();
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
    } catch {
      notify("error", "회원가입 실패", "잠시 후 다시 시도해 주세요.");
    }
  };

  return (
    <div className="mx-auto max-w-md pt-8">
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
          <a
            className="block pt-1 text-center text-sm font-semibold text-brand-700"
            href={`${import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080"}/api/auth/login/google`}
          >
            구글로 회원가입
          </a>
        </form>
      </div>
    </div>
  );
};
