import { Button } from "@/components/atoms/Button";
import { useGetApplicationsQuery, useProcessApplicationMutation } from "@/api/baseApi";
import { useToast } from "@/components/organisms/ToastProvider";
import { EmptyState } from "@/components/molecules/EmptyState";
import { Skeleton } from "@/components/atoms/Skeleton";

interface ApplicationManagerProps {
  studyId: number;
}

export const ApplicationManager = ({ studyId }: ApplicationManagerProps) => {
  const { notify } = useToast();
  const { data: applications = [], isLoading } = useGetApplicationsQuery(studyId);
  const [processApplication, { isLoading: processing }] = useProcessApplicationMutation();
  const statusLabel = {
    PENDING: "대기",
    APPROVED: "승인",
    REJECTED: "거절"
  } as const;

  const handleProcess = async (appId: number, approved: boolean) => {
    try {
      await processApplication({ appId, approved }).unwrap();
      notify("success", approved ? "승인 완료" : "거절 완료", "가입 신청 상태가 업데이트되었습니다.");
    } catch {
      notify("error", "요청 실패", "가입 신청 상태를 변경하지 못했습니다.");
    }
  };

  return (
    <div className="panel p-4">
      <h3 className="mb-3 text-lg font-bold text-slate-900">가입 신청 대기 목록</h3>
      {isLoading ? (
        <div className="space-y-2">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      ) : applications.length > 0 ? (
        <ul className="space-y-2">
          {applications.map((app) => (
            <li className="flex items-center justify-between rounded-xl border border-slate-200 p-3" key={app.id}>
              <div>
                <p className="font-semibold">{app.applicantName}</p>
                <p className="text-xs text-slate-500">상태: {statusLabel[app.status]}</p>
              </div>
              <div className="flex gap-2">
                <Button disabled={processing} onClick={() => handleProcess(app.id, true)}>
                  승인
                </Button>
                <Button disabled={processing} variant="warning" onClick={() => handleProcess(app.id, false)}>
                  거절
                </Button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <EmptyState title="대기 중인 요청이 없습니다" description="모든 가입 요청을 처리했습니다." />
      )}
    </div>
  );
};
