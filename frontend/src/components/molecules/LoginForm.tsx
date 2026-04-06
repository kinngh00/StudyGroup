import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Input } from "@/components/atoms/Input";
import { Button } from "@/components/atoms/Button";
import { Spinner } from "@/components/atoms/Spinner";

const normalizeEmail = (value: string) => value.normalize("NFKC").replace(/[\u200B-\u200D\uFEFF]/g, "").trim();

const loginSchema = z.object({
  email: z.preprocess(
    (value) => (typeof value === "string" ? normalizeEmail(value) : value),
    z
      .string({ required_error: "이메일은 필수입니다." })
      .min(1, "이메일은 필수입니다.")
      .email("유효한 이메일을 입력해 주세요.")
  ),
  password: z.string({ required_error: "비밀번호는 필수입니다." }).min(1, "비밀번호는 필수입니다.").min(8, "비밀번호는 8자 이상이어야 합니다.")
});

export type LoginFormValues = z.infer<typeof loginSchema>;

interface LoginFormProps {
  onSubmit: (values: LoginFormValues) => void;
  loading?: boolean;
}

export const LoginForm = ({ onSubmit, loading }: LoginFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema)
  });

  return (
    <form className="panel space-y-4 p-7" onSubmit={handleSubmit(onSubmit)}>
      <h1 className="text-2xl font-bold text-slate-900">다시 만나서 반가워요</h1>
      <p className="-mt-2 text-sm text-slate-600">로그인하고 스터디 그룹을 관리해 보세요.</p>
      <Input placeholder="이메일" error={errors.email?.message} {...register("email")} />
      <Input type="password" placeholder="비밀번호" error={errors.password?.message} {...register("password")} />
      <Button className="w-full" disabled={loading} type="submit">
        {loading ? (
          <>
            <Spinner />
            로그인 중...
          </>
        ) : (
          "로그인"
        )}
      </Button>
    </form>
  );
};
