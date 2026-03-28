import { Link } from "react-router-dom";
import { useGetStudiesQuery } from "@/api/baseApi";
import { Skeleton } from "@/components/atoms/Skeleton";
import { EmptyState } from "@/components/molecules/EmptyState";

export const Sidebar = () => {
  const { data: studies, isLoading } = useGetStudiesQuery({ finished: false });

  return (
    <aside className="panel h-fit p-4">
      <nav className="space-y-3">
        <Link className="block rounded-xl px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-brand-50" to="/">
          메인
        </Link>
        <Link className="block rounded-xl px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-brand-50" to="/dashboard">
          대시보드
        </Link>
        <Link className="block rounded-xl px-3 py-2 text-sm font-medium text-slate-700 transition hover:bg-brand-50" to="/study/create">
          스터디 개설
        </Link>
      </nav>
      <div className="mt-6">
        <h3 className="mb-2 px-3 text-xs font-bold uppercase tracking-wide text-slate-500">가입한 스터디</h3>
        {isLoading ? (
          <div className="space-y-2 px-3">
            <Skeleton className="h-8 w-full" />
            <Skeleton className="h-8 w-full" />
            <Skeleton className="h-8 w-full" />
          </div>
        ) : studies && studies.length > 0 ? (
          <ul className="space-y-1">
            {studies.slice(0, 5).map((study) => (
              <li key={study.id}>
                <Link className="block rounded-xl px-3 py-2 text-sm text-slate-700 transition hover:bg-slate-100" to={`/study/${study.id}/inside`}>
                  {study.name}
                </Link>
              </li>
            ))}
          </ul>
        ) : (
          <EmptyState compact title="가입한 스터디가 없습니다" description="스터디를 생성하거나 가입하면 바로가기가 표시됩니다." />
        )}
      </div>
    </aside>
  );
};
