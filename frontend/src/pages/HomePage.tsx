import { useState } from "react";
import { SearchRequestForm } from "@/components/molecules/SearchRequestForm";
import { RecruitmentCard } from "@/components/molecules/RecruitmentCard";
import { useGetStudiesQuery } from "@/api/baseApi";
import { Skeleton } from "@/components/atoms/Skeleton";
import { EmptyState } from "@/components/molecules/EmptyState";

export const HomePage = () => {
  const [filters, setFilters] = useState<{ name?: string; finished?: boolean }>({
    name: "",
    finished: false
  });
  const { data: studies = [], isLoading, isError } = useGetStudiesQuery(filters);

  return (
    <div className="space-y-6">
      <section className="rounded-3xl bg-gradient-to-r from-brand-700 to-brand-500 p-8 text-white shadow-xl">
        <h1 className="text-3xl font-extrabold leading-tight">스터디를 찾고, 함께 성장해 보세요</h1>
        <p className="mt-3 text-sm text-indigo-100">필터 검색으로 모집 중인 스터디를 빠르게 찾을 수 있습니다.</p>
      </section>
      <SearchRequestForm onSubmit={setFilters} />
      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {isLoading ? (
          <>
            <Skeleton className="h-56 w-full" />
            <Skeleton className="h-56 w-full" />
            <Skeleton className="h-56 w-full" />
          </>
        ) : isError ? (
          <div className="md:col-span-2 xl:col-span-3">
            <EmptyState title="스터디 목록을 불러오지 못했습니다" description="잠시 후 다시 시도해 주세요." />
          </div>
        ) : studies.length > 0 ? (
          studies.map((study) => <RecruitmentCard key={study.id} study={study} />)
        ) : (
          <div className="md:col-span-2 xl:col-span-3">
            <EmptyState title="검색 결과가 없습니다" description="다른 키워드를 입력하거나 종료된 스터디 포함 옵션을 확인해 주세요." />
          </div>
        )}
      </section>
    </div>
  );
};
