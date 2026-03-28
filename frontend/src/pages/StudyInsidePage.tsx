import { useState } from "react";
import { useParams } from "react-router-dom";
import {
  useCreateNoticeMutation,
  useCreateScheduleMutation,
  useDeleteRecruitmentMutation,
  useDeleteScheduleMutation,
  useGetNoticesQuery,
  useGetRecruitmentsQuery,
  useGetSchedulesQuery,
  useGetStudyMembersQuery,
  useUpdateRecruitmentMutation,
  useUpdateScheduleMutation
} from "@/api/baseApi";
import { ScheduleItem } from "@/components/molecules/ScheduleItem";
import { MemberListItem } from "@/components/molecules/MemberListItem";
import { Button } from "@/components/atoms/Button";
import { Input } from "@/components/atoms/Input";
import { useAppSelector } from "@/app/hooks";
import { EmptyState } from "@/components/molecules/EmptyState";
import { Skeleton } from "@/components/atoms/Skeleton";
import { useToast } from "@/components/organisms/ToastProvider";
import type { Recruitment, StudySchedule } from "@/types/domain";

type Tab = "dashboard" | "schedules" | "members" | "recruitments" | "notices";

export const StudyInsidePage = () => {
  const [tab, setTab] = useState<Tab>("dashboard");
  const { studyId } = useParams();
  const numericStudyId = Number(studyId);
  const role = useAppSelector((state) => state.study.userRoleInCurrentStudy);
  const canManage = role === "Owner" || role === "Admin";
  const { notify } = useToast();

  const { data: schedules = [], isLoading: schedulesLoading } = useGetSchedulesQuery(numericStudyId);
  const { data: members = [], isLoading: membersLoading } = useGetStudyMembersQuery(numericStudyId);
  const { data: recruitments = [], isLoading: recruitmentsLoading } = useGetRecruitmentsQuery({ studyId: numericStudyId });
  const { data: notices = [], isLoading: noticesLoading } = useGetNoticesQuery(numericStudyId);

  const [createSchedule, { isLoading: creatingSchedule }] = useCreateScheduleMutation();
  const [updateSchedule, { isLoading: updatingSchedule }] = useUpdateScheduleMutation();
  const [deleteSchedule, { isLoading: deletingSchedule }] = useDeleteScheduleMutation();
  const [updateRecruitment, { isLoading: updatingRecruitment }] = useUpdateRecruitmentMutation();
  const [deleteRecruitment, { isLoading: deletingRecruitment }] = useDeleteRecruitmentMutation();
  const [createNotice, { isLoading: creatingNotice }] = useCreateNoticeMutation();

  const [title, setTitle] = useState("");
  const [dateTime, setDateTime] = useState("");
  const [description, setDescription] = useState("");
  const [editingScheduleId, setEditingScheduleId] = useState<number | null>(null);

  const [editRecruitmentId, setEditRecruitmentId] = useState<number | null>(null);
  const [recruitmentTitle, setRecruitmentTitle] = useState("");
  const [recruitmentContent, setRecruitmentContent] = useState("");
  const [recruitmentDeadline, setRecruitmentDeadline] = useState("");
  const [recruitmentStatus, setRecruitmentStatus] = useState<"OPEN" | "CLOSED">("OPEN");

  const [noticeTitle, setNoticeTitle] = useState("");
  const [noticeContent, setNoticeContent] = useState("");

  if (Number.isNaN(numericStudyId)) {
    return <EmptyState title="잘못된 접근입니다" description="스터디 ID를 확인해 주세요." />;
  }

  const startScheduleEdit = (schedule: StudySchedule) => {
    setEditingScheduleId(schedule.id);
    setTitle(schedule.title);
    setDateTime(schedule.dateTime.slice(0, 16));
    setDescription(schedule.description ?? "");
  };

  const startRecruitmentEdit = (recruitment: Recruitment) => {
    setEditRecruitmentId(recruitment.id);
    setRecruitmentTitle(recruitment.title);
    setRecruitmentContent(recruitment.content);
    setRecruitmentDeadline(recruitment.deadline.slice(0, 10));
    setRecruitmentStatus(recruitment.status);
  };

  const createScheduleItem = async () => {
    if (!title || !dateTime) {
      notify("info", "입력값이 필요합니다", "제목과 일시를 모두 입력해 주세요.");
      return;
    }

    try {
      if (editingScheduleId) {
        await updateSchedule({ studyId: numericStudyId, scheduleId: editingScheduleId, title, dateTime, description }).unwrap();
        notify("success", "일정 수정 완료");
      } else {
        await createSchedule({ studyId: numericStudyId, title, dateTime, description }).unwrap();
        notify("success", "일정 생성 완료");
      }

      setEditingScheduleId(null);
      setTitle("");
      setDateTime("");
      setDescription("");
    } catch {
      notify("error", "요청 실패", "일정 처리 중 문제가 발생했습니다.");
    }
  };

  const removeScheduleItem = async (scheduleId: number) => {
    try {
      await deleteSchedule({ studyId: numericStudyId, scheduleId }).unwrap();
      notify("success", "일정 삭제 완료");
    } catch {
      notify("error", "삭제 실패", "일정을 삭제하지 못했습니다.");
    }
  };

  const submitRecruitmentEdit = async () => {
    if (!editRecruitmentId || !recruitmentTitle || !recruitmentContent || !recruitmentDeadline) {
      notify("info", "입력값이 필요합니다", "모집글 필수값을 모두 입력해 주세요.");
      return;
    }

    try {
      await updateRecruitment({
        studyId: numericStudyId,
        recruitmentId: editRecruitmentId,
        title: recruitmentTitle,
        content: recruitmentContent,
        deadline: recruitmentDeadline,
        status: recruitmentStatus
      }).unwrap();
      setEditRecruitmentId(null);
      setRecruitmentTitle("");
      setRecruitmentContent("");
      setRecruitmentDeadline("");
      setRecruitmentStatus("OPEN");
      notify("success", "모집글 수정 완료");
    } catch {
      notify("error", "수정 실패", "모집글을 수정하지 못했습니다.");
    }
  };

  const removeRecruitment = async (recruitmentId: number) => {
    try {
      await deleteRecruitment({ studyId: numericStudyId, recruitmentId }).unwrap();
      notify("success", "모집글 삭제 완료");
    } catch {
      notify("error", "삭제 실패", "모집글을 삭제하지 못했습니다.");
    }
  };

  const submitNotice = async () => {
    if (!noticeTitle || !noticeContent) {
      notify("info", "입력값이 필요합니다", "공지 제목과 내용을 입력해 주세요.");
      return;
    }

    try {
      await createNotice({ studyId: numericStudyId, title: noticeTitle, content: noticeContent }).unwrap();
      setNoticeTitle("");
      setNoticeContent("");
      notify("success", "공지 등록 완료");
    } catch {
      notify("error", "등록 실패", "공지를 등록하지 못했습니다.");
    }
  };

  return (
    <div className="space-y-4">
      <div className="panel flex gap-2 overflow-x-auto p-2">
        {(["dashboard", "schedules", "members", "recruitments", "notices"] as Tab[]).map((item) => (
          <Button key={item} onClick={() => setTab(item)} variant={tab === item ? "primary" : "ghost"}>
            {item === "dashboard" ? "대시보드" : item === "schedules" ? "일정" : item === "members" ? "회원" : item === "recruitments" ? "모집글" : "공지"}
          </Button>
        ))}
      </div>

      {tab === "dashboard" ? (
        <section className="panel p-6">
          <h2 className="text-xl font-bold text-slate-900">스터디 대시보드</h2>
          <p className="mt-2 text-sm text-slate-600">다음 일정: {schedules[0] ? new Date(schedules[0].dateTime).toLocaleString() : "미정"}</p>
          <p className="mt-2 text-sm text-slate-600">최근 공지: {notices[0] ? notices[0].title : "등록된 공지가 없습니다."}</p>
        </section>
      ) : null}

      {tab === "schedules" ? (
        <section className="panel p-6">
          <h2 className="mb-3 text-xl font-bold text-slate-900">일정 관리</h2>
          {canManage ? (
            <div className="mb-3 grid gap-2 md:grid-cols-[1fr_240px_auto]">
              <Input className="md:col-span-3" placeholder="일정 제목" value={title} onChange={(e) => setTitle(e.target.value)} />
              <Input type="datetime-local" value={dateTime} onChange={(e) => setDateTime(e.target.value)} />
              <Input placeholder="설명 (선택)" value={description} onChange={(e) => setDescription(e.target.value)} />
              <Button disabled={creatingSchedule || updatingSchedule} onClick={createScheduleItem}>
                {editingScheduleId ? "수정 저장" : "생성"}
              </Button>
            </div>
          ) : null}
          {schedulesLoading ? (
            <div className="space-y-2">
              <Skeleton className="h-16 w-full" />
              <Skeleton className="h-16 w-full" />
            </div>
          ) : schedules.length > 0 ? (
            <ul className="space-y-2">
              {schedules.map((schedule) => (
                <li key={schedule.id}>
                  <ScheduleItem schedule={schedule} />
                  {canManage ? (
                    <div className="mt-2 flex gap-2">
                      <Button variant="secondary" onClick={() => startScheduleEdit(schedule)}>
                        수정
                      </Button>
                      <Button disabled={deletingSchedule} variant="warning" onClick={() => removeScheduleItem(schedule.id)}>
                        삭제
                      </Button>
                    </div>
                  ) : null}
                </li>
              ))}
            </ul>
          ) : (
            <EmptyState title="등록된 일정이 없습니다" description="첫 일정을 등록해 스터디 일정을 공유해 보세요." />
          )}
        </section>
      ) : null}

      {tab === "members" ? (
        <section className="panel p-6">
          <h2 className="mb-3 text-xl font-bold text-slate-900">회원 목록</h2>
          {membersLoading ? (
            <div className="space-y-2">
              <Skeleton className="h-14 w-full" />
              <Skeleton className="h-14 w-full" />
            </div>
          ) : members.length > 0 ? (
            <ul className="space-y-2">
              {members.map((member) => (
                <li key={member.id}>
                  <MemberListItem member={member} />
                </li>
              ))}
            </ul>
          ) : (
            <EmptyState title="회원이 없습니다" description="가입이 완료된 회원이 여기에 표시됩니다." />
          )}
        </section>
      ) : null}

      {tab === "recruitments" ? (
        <section className="panel p-6">
          <h2 className="mb-3 text-xl font-bold text-slate-900">모집글</h2>
          {canManage && editRecruitmentId ? (
            <div className="mb-4 space-y-2 rounded-xl border border-slate-200 bg-slate-50 p-3">
              <p className="text-sm font-semibold text-slate-700">모집글 수정</p>
              <Input placeholder="제목" value={recruitmentTitle} onChange={(e) => setRecruitmentTitle(e.target.value)} />
              <Input placeholder="내용" value={recruitmentContent} onChange={(e) => setRecruitmentContent(e.target.value)} />
              <Input type="date" value={recruitmentDeadline} onChange={(e) => setRecruitmentDeadline(e.target.value)} />
              <div className="flex gap-2">
                <Button variant={recruitmentStatus === "OPEN" ? "primary" : "secondary"} onClick={() => setRecruitmentStatus("OPEN")}>
                  모집중
                </Button>
                <Button variant={recruitmentStatus === "CLOSED" ? "primary" : "secondary"} onClick={() => setRecruitmentStatus("CLOSED")}>
                  모집완료
                </Button>
              </div>
              <div className="flex gap-2">
                <Button disabled={updatingRecruitment} onClick={submitRecruitmentEdit}>
                  저장
                </Button>
                <Button
                  variant="ghost"
                  onClick={() => {
                    setEditRecruitmentId(null);
                    setRecruitmentTitle("");
                    setRecruitmentContent("");
                    setRecruitmentDeadline("");
                    setRecruitmentStatus("OPEN");
                  }}
                >
                  취소
                </Button>
              </div>
            </div>
          ) : null}
          {recruitmentsLoading ? (
            <div className="space-y-2">
              <Skeleton className="h-16 w-full" />
              <Skeleton className="h-16 w-full" />
            </div>
          ) : recruitments.length > 0 ? (
            <ul className="space-y-2">
              {recruitments.map((recruitment) => (
                <li className="rounded-xl border border-slate-200 p-3" key={recruitment.id}>
                  <div className="flex items-center justify-between gap-2">
                    <p className="font-semibold">{recruitment.title}</p>
                    <span className="rounded-lg bg-slate-100 px-2 py-1 text-xs text-slate-700">{recruitment.status === "OPEN" ? "모집중" : "모집완료"}</span>
                  </div>
                  <p className="text-sm text-slate-700">{recruitment.content}</p>
                  <p className="mt-1 text-xs text-slate-500">마감일: {new Date(recruitment.deadline).toLocaleDateString()}</p>
                  {canManage ? (
                    <div className="mt-2 flex gap-2">
                      <Button variant="secondary" onClick={() => startRecruitmentEdit(recruitment)}>
                        수정
                      </Button>
                      <Button disabled={deletingRecruitment} variant="warning" onClick={() => removeRecruitment(recruitment.id)}>
                        삭제
                      </Button>
                    </div>
                  ) : null}
                </li>
              ))}
            </ul>
          ) : (
            <EmptyState title="모집글이 없습니다" description="아직 모집 정보가 등록되지 않았습니다." />
          )}
        </section>
      ) : null}

      {tab === "notices" ? (
        <section className="panel p-6">
          <h2 className="mb-3 text-xl font-bold text-slate-900">공지사항</h2>
          {canManage ? (
            <div className="mb-4 space-y-2">
              <Input placeholder="공지 제목" value={noticeTitle} onChange={(e) => setNoticeTitle(e.target.value)} />
              <Input placeholder="공지 내용" value={noticeContent} onChange={(e) => setNoticeContent(e.target.value)} />
              <Button disabled={creatingNotice} onClick={submitNotice}>
                공지 등록
              </Button>
            </div>
          ) : null}
          {noticesLoading ? (
            <div className="space-y-2">
              <Skeleton className="h-16 w-full" />
              <Skeleton className="h-16 w-full" />
            </div>
          ) : notices.length > 0 ? (
            <ul className="space-y-2">
              {notices.map((notice) => (
                <li key={notice.id} className="rounded-xl border border-slate-200 p-3">
                  <p className="font-semibold">{notice.title}</p>
                  <p className="mt-1 text-sm text-slate-700">{notice.content}</p>
                  <p className="mt-1 text-xs text-slate-500">
                    작성자: {notice.authorName} · {new Date(notice.createdAt).toLocaleString()}
                  </p>
                </li>
              ))}
            </ul>
          ) : (
            <EmptyState title="등록된 공지가 없습니다" description="운영자 또는 관리자가 공지를 등록할 수 있습니다." />
          )}
        </section>
      ) : null}
    </div>
  );
};
