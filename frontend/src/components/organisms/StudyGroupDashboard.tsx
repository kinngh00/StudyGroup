import { RecruitmentCard } from "@/components/molecules/RecruitmentCard";
import { useGetRecruitmentsQuery, useGetSchedulesQuery, useGetStudiesQuery } from "@/api/baseApi";
import { ScheduleItem } from "@/components/molecules/ScheduleItem";
import { Skeleton } from "@/components/atoms/Skeleton";
import { EmptyState } from "@/components/molecules/EmptyState";

export const StudyGroupDashboard = () => {
  const { data: studies = [], isLoading: studiesLoading } = useGetStudiesQuery({ finished: false });
  const firstStudyId = studies[0]?.id ?? 0;
  const { data: recruitments = [] } = useGetRecruitmentsQuery({ studyId: firstStudyId }, { skip: !firstStudyId });
  const { data: schedules = [] } = useGetSchedulesQuery(firstStudyId, { skip: !firstStudyId });

  return (
    <section className="space-y-6">
      <div>
        <h2 className="mb-3 text-xl font-bold text-slate-900">내 스터디</h2>
        {studiesLoading ? (
          <div className="grid gap-4 md:grid-cols-2">
            <Skeleton className="h-44 w-full" />
            <Skeleton className="h-44 w-full" />
          </div>
        ) : studies.length > 0 ? (
          <div className="grid gap-4 md:grid-cols-2">
            {studies.map((study) => (
              <RecruitmentCard key={study.id} study={study} />
            ))}
          </div>
        ) : (
          <EmptyState title="스터디가 없습니다" description="첫 스터디를 만들고 활동을 시작해 보세요." />
        )}
      </div>
      <div className="grid gap-4 lg:grid-cols-2">
        <div className="panel p-4">
          <h3 className="mb-2 font-bold text-slate-900">최신 모집글</h3>
          {recruitments.length > 0 ? (
            <ul className="space-y-2 text-sm text-slate-700">
              {recruitments.slice(0, 5).map((r) => (
                <li key={r.id} className="rounded-xl bg-slate-50 p-2.5">
                  {r.title}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-sm text-slate-500">최신 모집글이 없습니다.</p>
          )}
        </div>
        <div className="panel p-4">
          <h3 className="mb-2 font-bold text-slate-900">다가오는 일정</h3>
          {schedules.length > 0 ? (
            <ul className="space-y-2">
              {schedules.slice(0, 5).map((s) => (
                <li key={s.id}>
                  <ScheduleItem schedule={s} />
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-sm text-slate-500">등록된 일정이 없습니다.</p>
          )}
        </div>
      </div>
    </section>
  );
};
