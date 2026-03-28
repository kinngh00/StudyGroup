import { useState } from "react";
import { useParams } from "react-router-dom";
import {
  useAddToBlacklistMutation,
  useAssignAdminMutation,
  useGetBlacklistUsersQuery,
  useGetStudyMembersQuery,
  useKickMemberMutation,
  useRemoveFromBlacklistMutation,
  useReportUserMutation
} from "@/api/baseApi";
import { MemberListItem } from "@/components/molecules/MemberListItem";
import { Button } from "@/components/atoms/Button";
import { ApplicationManager } from "@/components/organisms/ApplicationManager";
import { EmptyState } from "@/components/molecules/EmptyState";
import { Skeleton } from "@/components/atoms/Skeleton";
import { useToast } from "@/components/organisms/ToastProvider";
import type { StudyPermission } from "@/types/domain";

const permissionOptions: { key: StudyPermission; label: string }[] = [
  { key: "RECRUITMENT_WRITE", label: "모집글 작성/관리" },
  { key: "APPLICATION_APPROVE", label: "가입 승인/거절" },
  { key: "MEMBER_MANAGE", label: "회원 관리" },
  { key: "SCHEDULE_MANAGE", label: "일정 관리" }
];

export const StudyManagePage = () => {
  const { studyId } = useParams();
  const numericStudyId = Number(studyId);
  const { notify } = useToast();

  const { data: members = [], isLoading } = useGetStudyMembersQuery(numericStudyId);
  const { data: blacklistUsers = [], isLoading: blacklistLoading } = useGetBlacklistUsersQuery(numericStudyId);

  const [kickMember, { isLoading: kicking }] = useKickMemberMutation();
  const [assignAdmin, { isLoading: assigning }] = useAssignAdminMutation();
  const [addToBlacklist, { isLoading: addingBlacklist }] = useAddToBlacklistMutation();
  const [removeFromBlacklist, { isLoading: removingBlacklist }] = useRemoveFromBlacklistMutation();
  const [reportUser, { isLoading: reporting }] = useReportUserMutation();

  const [adminTarget, setAdminTarget] = useState<number | null>(null);
  const [permissions, setPermissions] = useState<StudyPermission[]>([]);

  if (Number.isNaN(numericStudyId)) {
    return <EmptyState title="잘못된 접근입니다" description="스터디 ID를 확인해 주세요." />;
  }

  const togglePermission = (permission: StudyPermission) => {
    setPermissions((prev) => (prev.includes(permission) ? prev.filter((item) => item !== permission) : [...prev, permission]));
  };

  const handleAssign = async (memberId: number) => {
    try {
      await assignAdmin({ studyId: numericStudyId, memberId, permissions }).unwrap();
      setAdminTarget(null);
      setPermissions([]);
      notify("success", "관리자 권한 부여 완료");
    } catch {
      notify("error", "요청 실패", "관리자 권한을 부여하지 못했습니다.");
    }
  };

  const handleKick = async (memberId: number) => {
    try {
      await kickMember({ studyId: numericStudyId, memberId }).unwrap();
      notify("success", "회원 강퇴 완료");
    } catch {
      notify("error", "요청 실패", "회원을 강퇴하지 못했습니다.");
    }
  };

  const handleBlacklistAdd = async (memberId: number) => {
    try {
      await addToBlacklist({ studyId: numericStudyId, memberId }).unwrap();
      notify("success", "블랙리스트 등록 완료");
    } catch {
      notify("error", "요청 실패", "블랙리스트에 등록하지 못했습니다.");
    }
  };

  const handleBlacklistRemove = async (userId: number) => {
    try {
      await removeFromBlacklist({ studyId: numericStudyId, userId }).unwrap();
      notify("success", "블랙리스트 해제 완료");
    } catch {
      notify("error", "요청 실패", "블랙리스트 해제에 실패했습니다.");
    }
  };

  const handleReportUser = async (targetUserId: number) => {
    const reason = window.prompt("신고 사유를 입력해 주세요.");
    if (!reason) return;

    try {
      await reportUser({ studyId: numericStudyId, targetUserId, reason }).unwrap();
      notify("success", "사용자 신고 접수 완료");
    } catch {
      notify("error", "요청 실패", "사용자 신고를 접수하지 못했습니다.");
    }
  };

  return (
    <div className="space-y-4">
      <ApplicationManager studyId={numericStudyId} />

      <section className="panel p-6">
        <h2 className="mb-3 text-xl font-bold text-slate-900">회원 관리</h2>
        {isLoading ? (
          <div className="space-y-2">
            <Skeleton className="h-16 w-full" />
            <Skeleton className="h-16 w-full" />
          </div>
        ) : members.length > 0 ? (
          <div className="space-y-2">
            {members.map((member) => (
              <div className="rounded-xl border border-slate-200 p-2" key={member.id}>
                <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                  <MemberListItem member={member} />
                  <div className="flex flex-wrap gap-2">
                    <Button
                      disabled={assigning}
                      onClick={() => {
                        setAdminTarget(member.id);
                        setPermissions(member.permissions ?? []);
                      }}
                    >
                      관리자 지정
                    </Button>
                    <Button disabled={kicking} variant="warning" onClick={() => handleKick(member.id)}>
                      강퇴
                    </Button>
                    <Button disabled={addingBlacklist} variant="secondary" onClick={() => handleBlacklistAdd(member.id)}>
                      블랙리스트 등록
                    </Button>
                    <Button disabled={reporting} variant="secondary" onClick={() => handleReportUser(member.id)}>
                      신고
                    </Button>
                  </div>
                </div>

                {adminTarget === member.id ? (
                  <div className="mt-3 rounded-lg bg-slate-50 p-3">
                    <p className="mb-2 text-sm font-semibold text-slate-700">관리자 세부 권한 선택</p>
                    <div className="grid gap-2 md:grid-cols-2">
                      {permissionOptions.map((option) => (
                        <label key={option.key} className="flex items-center gap-2 rounded-md border border-slate-200 bg-white px-2 py-1.5 text-sm text-slate-700">
                          <input checked={permissions.includes(option.key)} onChange={() => togglePermission(option.key)} type="checkbox" />
                          {option.label}
                        </label>
                      ))}
                    </div>
                    <div className="mt-2 flex gap-2">
                      <Button disabled={assigning} onClick={() => handleAssign(member.id)}>
                        권한 저장
                      </Button>
                      <Button
                        variant="ghost"
                        onClick={() => {
                          setAdminTarget(null);
                          setPermissions([]);
                        }}
                      >
                        취소
                      </Button>
                    </div>
                  </div>
                ) : null}
              </div>
            ))}
          </div>
        ) : (
          <EmptyState title="회원이 없습니다" description="가입이 승인된 회원이 표시됩니다." />
        )}
      </section>

      <section className="panel p-6">
        <h2 className="mb-3 text-xl font-bold text-slate-900">블랙리스트 관리</h2>
        {blacklistLoading ? (
          <div className="space-y-2">
            <Skeleton className="h-12 w-full" />
            <Skeleton className="h-12 w-full" />
          </div>
        ) : blacklistUsers.length > 0 ? (
          <ul className="space-y-2">
            {blacklistUsers.map((user) => (
              <li key={user.userId} className="flex items-center justify-between rounded-xl border border-slate-200 p-3">
                <div>
                  <p className="font-semibold text-slate-800">{user.name}</p>
                  <p className="text-xs text-slate-500">{user.reason ?? "등록 사유 없음"}</p>
                </div>
                <Button disabled={removingBlacklist} variant="secondary" onClick={() => handleBlacklistRemove(user.userId)}>
                  블랙리스트 해제
                </Button>
              </li>
            ))}
          </ul>
        ) : (
          <EmptyState title="블랙리스트 대상이 없습니다" description="강퇴 또는 직접 등록한 사용자 목록이 여기에 표시됩니다." />
        )}
      </section>
    </div>
  );
};
