import { cn } from "@/utils/cn";

interface BadgeProps {
  children: string;
  tone?: "open" | "closed" | "role";
}

export const Badge = ({ children, tone = "role" }: BadgeProps) => (
  <span
    className={cn(
      "inline-flex rounded-full px-2.5 py-1 text-xs font-semibold",
      tone === "open" && "bg-emerald-100 text-emerald-700",
      tone === "closed" && "bg-slate-200 text-slate-700",
      tone === "role" && "bg-brand-100 text-brand-700"
    )}
  >
    {children}
  </span>
);
