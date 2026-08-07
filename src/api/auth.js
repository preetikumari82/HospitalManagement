import client from "./client";

export const login = (email, password) =>
  client.post("/api/auth/login", { email, password }).then((r) => r.data);

export const logout = () => client.post("/api/auth/logout").then((r) => r.data);
