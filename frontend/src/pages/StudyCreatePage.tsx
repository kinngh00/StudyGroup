import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/atoms/Input";
import { Button } from "@/components/atoms/Button";
import { Spinner } from "@/components/atoms/Spinner";
import { useCreateStudyMutation } from "@/api/baseApi";
import { useToast } from "@/components/organisms/ToastProvider";

interface StudyCreateValues {
  name: string;
  description: string;
  maxMemberCount: number;
}

export const StudyCreatePage = () => {
  const navigate = useNavigate();
  const { notify } = useToast();
  const [createStudy, { isLoading }] = useCreateStudyMutation();
  const { register, handleSubmit } = useForm<StudyCreateValues>();

  const onSubmit = async (values: StudyCreateValues) => {
    try {
      await createStudy(values).unwrap();
      notify("success", "스터디 생성 완료", "스터디 그룹이 생성되었습니다.");
      navigate("/dashboard");
    } catch {
      notify("error", "생성 실패", "입력값을 확인하고 다시 시도해 주세요.");
    }
  };

  return (
    <div className="panel p-6">
      <h1 className="mb-1 text-2xl font-bold text-slate-900">새 스터디 그룹 만들기</h1>
      <p className="mb-4 text-sm text-slate-600">기본 정보를 입력하면 생성 후 수정할 수 있습니다.</p>
      <form className="grid gap-3 md:grid-cols-2" onSubmit={handleSubmit(onSubmit)}>
        <Input placeholder="스터디 이름" {...register("name")} />
        <Input type="number" placeholder="최대 인원" {...register("maxMemberCount", { valueAsNumber: true })} />
        <Input className="md:col-span-2" placeholder="소개" {...register("description")} />
        <Button className="md:col-span-2" disabled={isLoading} type="submit">
          {isLoading ? (
            <>
              <Spinner />
              생성 중...
            </>
          ) : (
            "스터디 생성"
          )}
        </Button>
      </form>
    </div>
  );
};
