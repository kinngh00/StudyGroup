import { createContext, useCallback, useContext, useMemo, useState, type PropsWithChildren } from "react";

type ToastType = "success" | "error" | "info";

interface ToastItem {
  id: number;
  type: ToastType;
  title: string;
  message?: string;
}

interface ToastContextValue {
  notify: (type: ToastType, title: string, message?: string) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

export const ToastProvider = ({ children }: PropsWithChildren) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const notify = useCallback((type: ToastType, title: string, message?: string) => {
    const id = Date.now() + Math.floor(Math.random() * 1000);
    setToasts((prev) => [...prev, { id, type, title, message }]);
    window.setTimeout(() => {
      setToasts((prev) => prev.filter((toast) => toast.id !== id));
    }, 3200);
  }, []);

  const value = useMemo(() => ({ notify }), [notify]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="pointer-events-none fixed right-4 top-4 z-50 flex w-full max-w-sm flex-col gap-2">
        {toasts.map((toast) => (
          <div
            className={[
              "rounded-xl border px-4 py-3 shadow-xl transition-all duration-300",
              toast.type === "success" && "border-emerald-200 bg-emerald-50 text-emerald-800",
              toast.type === "error" && "border-rose-200 bg-rose-50 text-rose-800",
              toast.type === "info" && "border-brand-200 bg-brand-50 text-brand-800"
            ]
              .filter(Boolean)
              .join(" ")}
            key={toast.id}
          >
            <p className="text-sm font-semibold">{toast.title}</p>
            {toast.message ? <p className="mt-1 text-xs">{toast.message}</p> : null}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const ctx = useContext(ToastContext);
  if (!ctx) {
    throw new Error("useToast must be used within ToastProvider");
  }
  return ctx;
};
