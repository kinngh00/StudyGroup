import { useParams, useNavigate } from "react-router-dom";
import { Button } from "@/components/atoms/Button";
import { Skeleton } from "@/components/atoms/Skeleton";
import { useAppSelector } from "@/app/hooks";
import { useApplyStudyMutation, useGetRecruitmentsQuery, useGetStudyQuery } from "@/api/baseApi";
import { EmptyState } from "@/components/molecules/EmptyState";
import { useToast } from "@/components/organisms/ToastProvider";

export const StudyDetailPage = () => {
  const navigate = useNavigate();
  const { notify } = useToast();
  const { studyId } = useParams();
  const numericStudyId = Number(studyId);
  const { isAuthenticated } = useAppSelector((state) => state.auth);
  const { data: study, isLoading, isError } = useGetStudyQuery(numericStudyId, { skip: Number.isNaN(numericStudyId) });
  const { data: recruitment = [], isLoading: recruitmentLoading } = useGetRecruitmentsQuery(
    { studyId: numericStudyId },
    { skip: Number.isNaN(numericStudyId) }
  );
  const [applyStudy, { isLoading: applying }] = useApplyStudyMutation();

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-56 w-full" />
        <Skeleton className="h-52 w-full" />
      </div>
    );
  }

  if (isError || !study) {
    return <EmptyState title="스터디를 찾을 수 없습니다" description="삭제되었거나 접근할 수 없는 스터디입니다." />;
  }

  const onApply = async () => {
    if (!isAuthenticated) {
      notify("info", "로그인이 필요합니다", "가입 신청 전에 로그인해 주세요.");
      navigate("/login");
      return;
    }

    const openRecruitment = recruitment.find((post) => post.status === "OPEN");
    if (!openRecruitment) {
      notify("info", "모집글 없음", "현재 신청 가능한 모집글이 없습니다.");
      return;
    }

    const motivation = window.prompt("지원 동기를 입력해 주세요.", "스터디에 참여하고 싶습니다.") ?? "스터디에 참여하고 싶습니다.";

    try {
      await applyStudy({
        studyId: study.id,
        recruitmentPostId: openRecruitment.id,
        motivation
      }).unwrap();
      notify("success", "가입 신청 완료");
    } catch {
      notify("error", "가입 신청 실패", "잠시 후 다시 시도해 주세요.");
    }
  };

  return (
    <div className="space-y-4">
      <section className="panel p-6">
        <h1 className="text-2xl font-bold text-slate-900">{study.name}</h1>
        <p className="mt-2 text-slate-700">{study.description}</p>
        <div className="mt-4 flex flex-wrap gap-3 text-sm text-slate-600">
          <span>정원: {study.maxMembers}명</span>
          <span>현재 인원: {study.currentMembers}명</span>
          <span>운영자: {study.ownerName ?? "-"}</span>
          <span>상태: {study.isFinished ? "모집완료" : "모집중"}</span>
        </div>
        <Button className="mt-4" disabled={applying} onClick={onApply}>
          {applying ? "신청 중..." : "가입 신청"}
        </Button>
        {isAuthenticated ? (
          <div className="mt-2 flex flex-wrap gap-2">
            <Button variant="secondary" onClick={() => navigate(`/study/${study.id}/inside`)}>
              스터디 내부 이동
            </Button>
            <Button variant="secondary" onClick={() => navigate(`/study/${study.id}/recruitment/create`)}>
              모집글 작성
            </Button>
            <Button variant="secondary" onClick={() => navigate(`/study/${study.id}/manage`)}>
              관리 페이지
            </Button>
          </div>
        ) : null}
      </section>
      <section className="panel p-6">
        <h2 className="mb-3 text-xl font-bold text-slate-900">모집글</h2>
        {recruitmentLoading ? (
          <div className="space-y-2">
            <Skeleton className="h-16 w-full" />
            <Skeleton className="h-16 w-full" />
          </div>
        ) : recruitment.length > 0 ? (
          <ul className="space-y-2">
            {recruitment.map((post) => (
              <li key={post.id} className="rounded-xl border border-slate-200 p-3">
                <div className="flex items-center justify-between gap-2">
                  <p className="font-semibold">{post.title}</p>
                  <span className="rounded-lg bg-slate-100 px-2 py-1 text-xs text-slate-700">{post.status === "OPEN" ? "모집중" : "모집완료"}</span>
                </div>
                <p className="text-sm text-slate-600">{post.content}</p>
              </li>
            ))}
          </ul>
        ) : (
          <EmptyState title="등록된 모집글이 없습니다" description="아직 이 스터디의 모집글이 작성되지 않았습니다." />
        )}
      </section>
    </div>
  );
};
