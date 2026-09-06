import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { RoleBadge } from "./Atoms";
import { useState } from "react";

const NAV_BY_ROLE = {
  ADMIN: [
    { to: "/admin/doctors", label: "Doctors", icon: "🩺" },
    { to: "/admin/medical", label: "Medical Staff", icon: "💊" },
    { to: "/admin/staff", label: "Staff Accounts", icon: "👥" },
    { to: "/admin/patients", label: "Patients", icon: "🧑‍⚕️" },

  ],

  RECEPTIONIST: [{ to: "/admin/patients", label: "Patients", icon: "🧑‍⚕️" }, { to: "/appointments", label: "Appointments", icon: "📅" }, { to: "/billing", label: "Billing", icon: "🧾" }],
  PHARMACIST: [{ to: "/admin/patients", label: "Patients", icon: "💊" }, { to: "/appointments", label: "Appointments", icon: "📅" }],

  LAB_TECH: [{ to: "/admin/patients", label: "Patients", icon: "🧪" }],
  DOCTOR: [
    { to: "/doctor/patients", label: "Patients", icon: "🛏️" },
    { to: "/doctor/nurses", label: "Nurses", icon: "👩‍⚕️" },
    { to: "/doctor/schedule", label: "Schedule", icon: "🗓️" },
    { to: "/doctor/leave", label: "Leave", icon: "🌴" },
    { to: "/appointments", label: "Appointments", icon: "📅" },
    { to: "/ehr", label: "EHR", icon: "📋" },
  ],
  NURSE: [{ to: "/nurse/patients", label: "Patients", icon: "🛏️" }],
  PATIENT: [{ to: "/appointments", label: "Appointments", icon: "📅" }, { to: "/ehr", label: "EHR & Reports", icon: "📋" }, { to: "/billing", label: "Bills", icon: "🧾" }, { to: "/patient/profile", label: "Profile", icon: "👤" }],
  MEDICAL: [{ to: "/medical/patients", label: "Pharmacy & Billing", icon: "🧾" }, { to: "/billing", label: "Billing", icon: "💳" }],
};

const ROLE_TITLE = {
  ADMIN: "Administrator",
  DOCTOR: "Doctor",
  NURSE: "Nurse",
  MEDICAL: "Pharmacy / Billing",
  RECEPTIONIST: "Reception",
  PHARMACIST: "Pharmacist",
  LAB_TECH: "Lab Technician",
};

export default function Layout() {
  const { user, logout } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [mobileOpen, setMobileOpen] = useState(false);
  const nav = NAV_BY_ROLE[user?.role] || [];

  const handleLogout = async () => {
    await logout();
    toast.info("Logged out. See you soon.");
    navigate("/login", { replace: true });
  };

  return (
    <div className="min-h-screen flex">
      {/* Sidebar - desktop */}
      <aside className="hidden lg:flex w-64 shrink-0 flex-col border-r border-line bg-white">
        <Brand />
        <nav className="flex-1 px-3 py-4 space-y-1">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors ${
                  isActive ? "bg-clover-700 text-white shadow-sm" : "text-ink-600 hover:bg-ink-100"
                }`
              }
            >
              <span>{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <UserBox user={user} onLogout={handleLogout} />
      </aside>

      {/* Sidebar - mobile drawer */}
      {mobileOpen && (
        <div className="fixed inset-0 z-40 lg:hidden">
          <div className="fixed inset-0 bg-ink-950/50" onClick={() => setMobileOpen(false)} />
          <aside className="relative z-50 flex h-full w-64 flex-col bg-white animate-fade-in">
            <Brand />
            <nav className="flex-1 px-3 py-4 space-y-1">
              {nav.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  onClick={() => setMobileOpen(false)}
                  className={({ isActive }) =>
                    `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium ${
                      isActive ? "bg-clover-700 text-white" : "text-ink-600 hover:bg-ink-100"
                    }`
                  }
                >
                  <span>{item.icon}</span>
                  {item.label}
                </NavLink>
              ))}
            </nav>
            <UserBox user={user} onLogout={handleLogout} />
          </aside>
        </div>
      )}

      {/* Main column */}
      <div className="flex-1 min-w-0 flex flex-col">
        <header className="lg:hidden flex items-center justify-between border-b border-line bg-white px-4 py-3">
          <button onClick={() => setMobileOpen(true)} className="btn-ghost px-2">
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
          <span className="font-display font-semibold text-ink-900">Meridian Care</span>
          <RoleBadge role={user?.role} />
        </header>

        <div className="hidden lg:flex items-center justify-between border-b border-line bg-white/70 backdrop-blur px-8 py-3 sticky top-0 z-30">
          <p className="text-xs font-semibold text-ink-400">{ROLE_TITLE[user?.role]} Dashboard</p>
          <div className="flex items-center gap-3">
            <span className="text-sm text-ink-500">{new Date().toLocaleDateString(undefined, { weekday: "long", year: "numeric", month: "long", day: "numeric" })}</span>
          </div>
        </div>

        <main className="flex-1 p-4 sm:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

function Brand() {
  return (
    <div className="flex items-center gap-2.5 px-5 py-5 border-b border-line">
      <div className="h-9 w-9 rounded-lg bg-clover-700 grid place-items-center text-white font-display font-bold">M</div>
      <div>
        <p className="font-display font-semibold text-ink-900 leading-none">Meridian Care</p>
        <p className="text-[11px] text-ink-400 mt-0.5">Hospital Operations</p>
      </div>
    </div>
  );
}

function UserBox({ user, onLogout }) {
  return (
    <div className="border-t border-line p-4">
      <div className="flex items-center gap-3 mb-3">
        <div className="h-9 w-9 rounded-full bg-ink-800 text-white grid place-items-center text-sm font-semibold shrink-0">
          {(user?.name || "?").slice(0, 1).toUpperCase()}
        </div>
        <div className="min-w-0">
          <p className="text-sm font-semibold text-ink-900 truncate">{user?.name}</p>
          <p className="text-xs text-ink-400 truncate">{user?.email}</p>
        </div>
      </div>
      <button onClick={onLogout} className="btn-outline w-full text-xs">
        Log out
      </button>
    </div>
  );
}
