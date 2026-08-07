import axios from "axios";

// Base URL of the Spring Boot backend. Override at build time with
// VITE_API_BASE_URL if the backend runs somewhere other than localhost:8080.
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const client = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// Attach the JWT (if present) to every outgoing request.
client.interceptors.request.use((config) => {
  const token = localStorage.getItem("hms_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Normalize error messages coming back from Spring's GlobalExceptionHandler,
// and force a logout if the token has expired / is invalid (401/403).
client.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;
    const data = error?.response?.data;

    let message =
      (typeof data === "string" && data) ||
      data?.message ||
      data?.error ||
      error.message ||
      "Something went wrong. Please try again.";

    if (status === 401 || status === 403) {
      // Token missing/expired/invalid or insufficient role.
      if (status === 401) {
        localStorage.removeItem("hms_token");
        localStorage.removeItem("hms_user");
        message = "Your session has expired. Please log in again.";
        window.dispatchEvent(new CustomEvent("hms:unauthorized"));
      } else {
        message = "You don't have permission to do that.";
      }
    }

    return Promise.reject({ ...error, friendlyMessage: message });
  }
);

export default client;
