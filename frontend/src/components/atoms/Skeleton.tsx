import { cn } from "@/utils/cn";

interface SkeletonProps {
  className?: string;
}

export const Skeleton = ({ className }: SkeletonProps) => (
  <div className={cn("animate-pulse rounded-xl bg-slate-200/70", className)} />
);
