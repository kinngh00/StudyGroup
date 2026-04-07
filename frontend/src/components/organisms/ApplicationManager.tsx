import { useMemo, useState } from "react";
import { Button } from "@/components/atoms/Button";
import {
  useApproveApplicationMutation,
  useGetApplicationsQuery,
  useGetRecruitmentsQuery,
  useRejectApplicationMutation
} from "@/api/baseApi";
import { useToast } from "@/components/organisms/ToastProvider";
import { EmptyState } from "@/components/molecules/EmptyState";
import { Skeleton } from "@/components/atoms/Skeleton";

interface ApplicationManagerProps {
  studyId: number;
}

export const ApplicationManager = ({ studyId }: ApplicationManagerProps) => {
  const { notify } = useToast();
  const { data: recruitments = [], isLoading: recruitmentsLoading } = useGetRecruitmentsQuery({ studyId });

  const firstRecruitmentId = useMemo(() => {
    const openRecruitment = recruitments.find((recruitment) => recruitment.status === "OPEN");
    return openRecruitment?.id ?? recruitments[0]?.id;
  }, [recruitments]);

  const [selectedRecruitmentId, setSelectedRecruitmentId] = useState<number | undefined>(undefined);
  const recruitmentPostId = selectedRecruitmentId ?? firstRecruitmentId;

  const { data: applications = [], isLoading } = useGetApplicationsQuery(
    { studyId, recruitmentPostId: recruitmentPostId ?? 0 },
    { skip: !recruitmentPostId }
  );

  const [approveApplication, { isLoading: approving }] = useApproveApplicationMutation();
  const [rejectApplication, { isLoading: rejecting }] = useRejectApplicationMutation();

  const statusLabel = {
    PENDING: "대기",
    APPROVED: "승인",
    REJECTED: "거절"
  } as const;

  const handleApprove = async (applicationId: number) => {
    if (!recruitmentPostId) {
      return;
    }
    try {
      await approveApplication({ studyId, recruitmentPostId, applicationId }).unwrap();
      notify("success", "승인 완료", "가입 신청 상태가 업데이트되었습니다.");
    } catch {
      notify("error", "요청 실패", "가입 신청 상태를 변경하지 못했습니다.");
    }
  };

  const handleReject = async (applicationId: number) => {
    if (!recruitmentPostId) {
      return;
    }
    try {
      await rejectApplication({ studyId, recruitmentPostId, applicationId }).unwrap();
      notify("success", "거절 완료", "가입 신청 상태가 업데이트되었습니다.");
    } catch {
      notify("error", "요청 실패", "가입 신청 상태를 변경하지 못했습니다.");
    }
  };

  if (recruitmentsLoading) {
    return (
      <div className="panel p-4">
        <h3 className="mb-3 text-lg font-bold text-slate-900">가입 신청 대기 목록</h3>
        <div className="space-y-2">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      </div>
    );
  }

  if (recruitments.length === 0) {
    return (
      <div className="panel p-4">
        <h3 className="mb-3 text-lg font-bold text-slate-900">가입 신청 대기 목록</h3>
        <EmptyState title="모집글이 없습니다" description="가입 신청을 받으려면 모집글을 먼저 작성해 주세요." />
      </div>
    );
  }

  return (
    <div className="panel p-4">
      <h3 className="mb-3 text-lg font-bold text-slate-900">가입 신청 대기 목록</h3>

      <div className="mb-3 flex flex-wrap gap-2">
        {recruitments.map((recruitment) => (
          <Button
            key={recruitment.id}
            onClick={() => setSelectedRecruitmentId(recruitment.id)}
            variant={(selectedRecruitmentId ?? firstRecruitmentId) === recruitment.id ? "primary" : "secondary"}
          >
            {recruitment.title}
          </Button>
        ))}
      </div>

      {isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      ) : applications.length > 0 ? (
        <ul className="space-y-2">
          {applications.map((application) => (
            <li className="flex items-center justify-between rounded-xl border border-slate-200 p-3" key={application.id}>
              <div>
                <p className="font-semibold">{application.applicantName}</p>
                <p className="text-xs text-slate-500">상태: {statusLabel[application.status]}</p>
                <p className="text-xs text-slate-500">지원동기: {application.motivation}</p>
              </div>
              {application.status === "PENDING" ? (
                <div className="flex gap-2">
                  <Button disabled={approving || rejecting} onClick={() => handleApprove(application.id)}>
                    승인
                  </Button>
                  <Button disabled={approving || rejecting} variant="warning" onClick={() => handleReject(application.id)}>
                    거절
                  </Button>
                </div>
              ) : null}
            </li>
          ))}
        </ul>
      ) : (
        <EmptyState title="대기 중인 요청이 없습니다" description="모든 가입 요청을 처리했습니다." />
      )}
    </div>
  );
};
