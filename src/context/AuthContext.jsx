import { createContext, useContext, useEffect, useMemo, useState, useCallback } from "react";
import * as authApi from "../api/auth";

const AuthContext = createContext(null);

function readStoredUser() {
  try {
    const raw = localStorage.getItem("hms_user");
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser);
  const [token, setToken] = useState(() => localStorage.getItem("hms_token"));
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const doLogin = useCallback(async (email, password) => {
    setLoading(true);
    setError("");
    try {
      const res = await authApi.login(email, password);
      const nextUser = { name: res.name, email: res.email, role: res.role, profileId: res.profileId ?? null };
      localStorage.setItem("hms_token", res.token);
      localStorage.setItem("hms_user", JSON.stringify(nextUser));
      setToken(res.token);
      setUser(nextUser);
      return nextUser;
    } catch (err) {
      const msg = err?.friendlyMessage || "Invalid email or password.";
      setError(msg);
      throw new Error(msg);
    } finally {
      setLoading(false);
    }
  }, []);

  const doLogout = useCallback(async () => {
    try {
      if (token) await authApi.logout();
    } catch {
      // ignore network errors on logout
    } finally {
      localStorage.removeItem("hms_token");
      localStorage.removeItem("hms_user");
      setToken(null);
      setUser(null);
    }
  }, [token]);

  useEffect(() => {
    const handler = () => {
      setToken(null);
      setUser(null);
    };
    window.addEventListener("hms:unauthorized", handler);
    return () => window.removeEventListener("hms:unauthorized", handler);
  }, []);

  const value = useMemo(
    () => ({ user, token, error, loading, login: doLogin, logout: doLogout, setError }),
    [user, token, error, loading, doLogin, doLogout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
