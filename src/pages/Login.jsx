import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useToast } from "../context/ToastContext";
import { ErrorText, Spinner } from "../components/Atoms";
import { roleHome } from "../utils/roleHome";

export default function Login() {
  const { login, loading, error, setError } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPw, setShowPw] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const user = await login(email, password);
      toast.success(`Welcome back, ${user.name.split(" ")[0]}.`);
      const dest = location.state?.from?.pathname || roleHome(user.role);
      navigate(dest, { replace: true });
    } catch {
      // error already set in context
    }
  };

  return (
    <div className="min-h-screen grid lg:grid-cols-2">
      {/* Left / brand panel */}
      <div className="hidden lg:flex flex-col justify-between bg-ink-950 text-paper p-12 relative overflow-hidden">
        <div className="absolute inset-0 opacity-[0.07]" style={{ backgroundImage: "radial-gradient(circle at 2px 2px, #fff 1px, transparent 0)", backgroundSize: "24px 24px" }} />
        <div className="relative flex items-center gap-3">
          <div className="h-10 w-10 rounded-lg bg-clover-500 grid place-items-center font-display font-bold text-ink-950">M</div>
          <span className="font-display text-lg font-semibold">Meridian Care</span>
        </div>
        <div className="relative">
          <p className="text-xs uppercase tracking-widest text-clover-400 font-semibold mb-4">Ward, pharmacy &amp; front-desk, in one place</p>
          <h1 className="font-display text-4xl xl:text-5xl font-medium leading-tight max-w-lg">
            One record, from admission to discharge.
          </h1>
          <p className="text-ink-300 mt-5 max-w-md text-sm leading-relaxed">
            Admins staff the wards, doctors admit and refer, nurses chart vitals and treatment,
            and pharmacy bills every dose — each role sees exactly what it needs, nothing more.
          </p>
        </div>
        <p className="relative text-xs text-ink-400">© {new Date().getFullYear()} Meridian Care Hospital Systems</p>
      </div>

      {/* Right / form panel */}
      <div className="flex items-center justify-center p-6 sm:p-10 bg-paper">
        <div className="w-full max-w-sm">
          <div className="lg:hidden flex items-center gap-2.5 mb-8">
            <div className="h-9 w-9 rounded-lg bg-clover-700 grid place-items-center text-white font-display font-bold">M</div>
            <span className="font-display font-semibold text-ink-900">Meridian Care</span>
          </div>

          <h2 className="font-display text-2xl font-semibold text-ink-900">Sign in</h2>
          <p className="text-sm text-ink-500 mt-1 mb-6">Use the credentials issued by your hospital administrator.</p>

          <form onSubmit={onSubmit} className="space-y-4">
            <div>
              <label className="label">Email or Username</label>
             <input
  type="text"
  required
  autoFocus
  value={email}
  onChange={(e) => setEmail(e.target.value)}
  placeholder="you@meridiancare.com"
  className="input"
/>
            </div>
            <div>
              <label className="label">Password</label>
              <div className="relative">
                <input
                  type={showPw ? "text" : "password"}
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="input pr-10"
                />
                <button
                  type="button"
                  onClick={() => setShowPw((s) => !s)}
                  className="absolute right-2.5 top-1/2 -translate-y-1/2 text-ink-400 hover:text-ink-700 text-xs font-semibold"
                >
                  {showPw ? "Hide" : "Show"}
                </button>
              </div>
            </div>

            <ErrorText>{error}</ErrorText>

            <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
              {loading ? <Spinner className="h-4 w-4" /> : "Sign in"}
            </button>
          </form>

          <div className="flex justify-between mt-4 text-sm"><a
  className="text-clover-700 font-semibold"
  href="/register"
>
  Patient self-register
</a>

<a
  className="text-clover-700 font-semibold"
  href="/forgot-password"
>
  Forgot password?
</a></div>

          <div className="mt-8 rounded-xl border border-line bg-white p-4 text-xs text-ink-500 leading-relaxed">
            <p className="font-semibold text-ink-700 mb-1">Roles on this system</p>
            <p>Admin · Doctor · Receptionist · Patient · Pharmacist · Lab Tech · Nurse. Patients can self-register; staff accounts are managed by Admin.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
