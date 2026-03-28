interface EmptyStateProps {
  title: string;
  description: string;
  compact?: boolean;
}

export const EmptyState = ({ title, description, compact = false }: EmptyStateProps) => (
  <div className={compact ? "rounded-xl border border-dashed border-slate-300 bg-slate-50/80 p-4 text-center" : "rounded-2xl border border-dashed border-slate-300 bg-slate-50/80 p-8 text-center"}>
    <div className={compact ? "mx-auto mb-2 h-8 w-8 rounded-full bg-brand-100" : "mx-auto mb-4 h-12 w-12 rounded-full bg-brand-100"} />
    <h3 className={compact ? "text-sm font-bold text-slate-900" : "text-lg font-bold text-slate-900"}>{title}</h3>
    <p className={compact ? "mt-1 text-xs text-slate-600" : "mt-2 text-sm text-slate-600"}>{description}</p>
  </div>
);
