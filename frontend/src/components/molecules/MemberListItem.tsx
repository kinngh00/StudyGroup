import { Badge } from "@/components/atoms/Badge";
import type { StudyMember } from "@/types/domain";

interface MemberListItemProps {
  member: StudyMember;
}

const roleLabel: Record<StudyMember["role"], string> = {
  Owner: "운영자",
  Admin: "관리자",
  Member: "회원",
  None: "비회원"
};

export const MemberListItem = ({ member }: MemberListItemProps) => (
  <div className="flex items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
    <div>
      <p className="font-medium">{member.name}</p>
      {member.permissions && member.permissions.length > 0 ? (
        <p className="mt-1 text-xs text-slate-500">권한: {member.permissions.join(", ")}</p>
      ) : null}
    </div>
    <Badge>{roleLabel[member.role]}</Badge>
  </div>
);
