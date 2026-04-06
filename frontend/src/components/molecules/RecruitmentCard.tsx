import { Link } from "react-router-dom";
import { Badge } from "@/components/atoms/Badge";
import type { Study } from "@/types/domain";

interface RecruitmentCardProps {
  study: Study;
}

export const RecruitmentCard = ({ study }: RecruitmentCardProps) => (
  <article className="panel group p-5 transition-all duration-200 hover:-translate-y-1 hover:shadow-xl">
    <div className="mb-3 flex items-start justify-between gap-3">
      <h3 className="text-lg font-semibold text-slate-900">{study.name}</h3>
      <Badge tone={study.isFinished ? "closed" : "open"}>{study.isFinished ? "모집 완료" : "모집 중"}</Badge>
    </div>
    <p className="mb-4 line-clamp-2 text-sm text-slate-700">{study.description || "스터디 소개가 아직 등록되지 않았습니다."}</p>
    <div className="mb-4 flex flex-wrap gap-2 text-xs text-slate-600">
      <span>정원: {study.maxMembers}명</span>
      <span>현재 인원: {study.currentMembers}명</span>
      {study.ownerName ? <span>운영자: {study.ownerName}</span> : null}
    </div>
    <Link className="text-sm font-semibold text-brand-700 underline-offset-2 hover:underline" to={`/study/${study.id}`}>
      상세 보기
    </Link>
  </article>
);
