import { useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import { Button } from "@/components/atoms/Button";
import { Input } from "@/components/atoms/Input";
import { useCreateRecruitmentMutation } from "@/api/baseApi";
import { useToast } from "@/components/organisms/ToastProvider";
import { Spinner } from "@/components/atoms/Spinner";
import { EmptyState } from "@/components/molecules/EmptyState";

interface RecruitmentCreateValues {
  title: string;
  content: string;
  deadline: string;
}

export const RecruitmentCreatePage = () => {
  const navigate = useNavigate();
  const { studyId } = useParams();
  const numericStudyId = Number(studyId);
  const { register, handleSubmit } = useForm<RecruitmentCreateValues>();
  const { notify } = useToast();
  const [createRecruitment, { isLoading }] = useCreateRecruitmentMutation();

  if (Number.isNaN(numericStudyId)) {
    return <EmptyState title="잘못된 접근입니다" description="스터디 ID를 확인해 주세요." />;
  }

  return (
    <div className="panel p-6">
      <h1 className="mb-1 text-2xl font-bold text-slate-900">모집글 작성</h1>
      <p className="mb-4 text-sm text-slate-600">지원자를 위한 모집 조건과 일정을 입력해 주세요.</p>
      <form
        className="space-y-3"
        onSubmit={handleSubmit(async (values) => {
          try {
            await createRecruitment({ studyId: numericStudyId, ...values }).unwrap();
            notify("success", "모집글 게시 완료");
            navigate(`/study/${numericStudyId}/inside`);
          } catch {
            notify("error", "게시 실패", "잠시 후 다시 시도해 주세요.");
          }
        })}
      >
        <Input placeholder="제목" {...register("title")} />
        <Input placeholder="내용" {...register("content")} />
        <Input type="date" {...register("deadline")} />
        <Button disabled={isLoading} type="submit">
          {isLoading ? (
            <>
              <Spinner />
              게시 중...
            </>
          ) : (
            "게시하기"
          )}
        </Button>
      </form>
    </div>
  );
};
