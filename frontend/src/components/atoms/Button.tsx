import type { ButtonHTMLAttributes } from "react";
import { cn } from "@/utils/cn";

type Variant = "primary" | "secondary" | "warning" | "ghost";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
}

const variants: Record<Variant, string> = {
  primary: "bg-brand-600 text-white hover:-translate-y-0.5 hover:bg-brand-700",
  secondary: "border border-slate-200 bg-white text-slate-800 hover:-translate-y-0.5 hover:bg-slate-50",
  warning: "bg-rose-600 text-white hover:-translate-y-0.5 hover:bg-rose-700",
  ghost: "bg-transparent text-slate-700 hover:bg-slate-100"
};

export const Button = ({ variant = "primary", className, ...props }: ButtonProps) => (
  <button
    className={cn(
      "inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2.5 text-sm font-semibold transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-300 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50",
      variants[variant],
      className
    )}
    {...props}
  />
);
