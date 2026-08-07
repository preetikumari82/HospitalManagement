import { createContext, useCallback, useContext, useRef, useState } from "react";

const ToastContext = createContext(null);

let idSeq = 1;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const timers = useRef({});

  const remove = useCallback((id) => {
    setToasts((t) => t.filter((x) => x.id !== id));
    clearTimeout(timers.current[id]);
    delete timers.current[id];
  }, []);

  const push = useCallback(
    (message, type = "info") => {
      const id = idSeq++;
      setToasts((t) => [...t, { id, message, type }]);
      timers.current[id] = setTimeout(() => remove(id), 4200);
    },
    [remove]
  );

  const api = {
    success: (m) => push(m, "success"),
    error: (m) => push(m, "error"),
    info: (m) => push(m, "info"),
  };

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="fixed bottom-5 right-5 z-[100] flex flex-col gap-2 w-80">
        {toasts.map((t) => (
          <div
            key={t.id}
            onClick={() => remove(t.id)}
            className={`animate-fade-in cursor-pointer rounded-xl border px-4 py-3 text-sm font-medium shadow-panel ${
              t.type === "success"
                ? "bg-clover-700 text-white border-clover-800"
                : t.type === "error"
                ? "bg-clay-500 text-white border-clay-600"
                : "bg-ink-900 text-white border-ink-950"
            }`}
          >
            {t.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast must be used within ToastProvider");
  return ctx;
}
