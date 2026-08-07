export function Spinner({ className = "h-5 w-5" }) {
  return (
    <svg className={`animate-spin ${className}`} viewBox="0 0 24 24" fill="none">
      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
      <path className="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
    </svg>
  );
}

export function FullPageLoader({ label = "Loading…" }) {
  return (
    <div className="flex h-[60vh] flex-col items-center justify-center gap-3 text-ink-400">
      <Spinner className="h-8 w-8" />
      <p className="text-sm font-medium">{label}</p>
    </div>
  );
}

const STATUS_STYLES = {
  ADMITTED: "bg-clover-100 text-clover-800",
  UNDER_TREATMENT: "bg-amber-100 text-amber-800",
  DISCHARGED: "bg-ink-100 text-ink-600",
  REFERRED: "bg-clay-400/20 text-clay-600",
};

export function StatusBadge({ status }) {
  if (!status) return <span className="text-ink-300">—</span>;
  const cls = STATUS_STYLES[status] || "bg-ink-100 text-ink-600";
  return <span className={`badge ${cls}`}>{status.replaceAll("_", " ")}</span>;
}

export function RoleBadge({ role }) {
  const styles = {
    ADMIN: "bg-ink-900 text-white",
    DOCTOR: "bg-clover-700 text-white",
    NURSE: "bg-clay-500 text-white",
    MEDICAL: "bg-amber-500 text-white",
    PATIENT: "bg-ink-200 text-ink-700",
    RECEPTIONIST: "bg-ink-200 text-ink-700",
  };
  return <span className={`badge ${styles[role] || "bg-ink-200 text-ink-700"}`}>{role}</span>;
}

export function StatCard({ label, value, icon, accent = "clover" }) {
  const accents = {
    clover: "bg-clover-700 text-white",
    clay: "bg-clay-500 text-white",
    ink: "bg-ink-800 text-white",
    amber: "bg-amber-500 text-white",
  };
  return (
    <div className="card p-5 flex items-center gap-4">
      <div className={`h-11 w-11 rounded-xl grid place-items-center shrink-0 ${accents[accent]}`}>{icon}</div>
      <div>
        <p className="text-2xl font-display font-semibold text-ink-900 leading-none">{value}</p>
        <p className="text-xs font-medium text-ink-500 mt-1">{label}</p>
      </div>
    </div>
  );
}

export function PageHeader({ eyebrow, title, subtitle, action }) {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between mb-6">
      <div>
        {eyebrow && <p className="text-xs font-bold uppercase tracking-widest text-clover-600 mb-1">{eyebrow}</p>}
        <h1 className="font-display text-2xl sm:text-3xl font-semibold text-ink-900">{title}</h1>
        {subtitle && <p className="text-sm text-ink-500 mt-1 max-w-xl">{subtitle}</p>}
      </div>
      {action}
    </div>
  );
}

const GRID_COLS = { 1: "sm:grid-cols-1", 2: "sm:grid-cols-2", 3: "sm:grid-cols-3", 4: "sm:grid-cols-4" };

export function FormRow({ children, cols = 2 }) {
  return <div className={`grid grid-cols-1 ${GRID_COLS[cols] || GRID_COLS[2]} gap-4`}>{children}</div>;
}

export function ErrorText({ children }) {
  if (!children) return null;
  return <p className="text-sm text-clay-600 bg-clay-400/10 border border-clay-400/30 rounded-lg px-3 py-2">{children}</p>;
}
