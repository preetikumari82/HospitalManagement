import client from "./client";

// ---- Doctors ----
export const registerDoctor = (payload) =>
  client.post("/api/admin/ragisterdoctor", payload).then((r) => r.data);

export const getAllDoctors = () =>
  client.get("/api/admin/getalldoctors").then((r) => r.data);

export const updateDoctor = (id, payload) =>
  client.put(`/api/admin/updatedoctor/${id}`, payload).then((r) => r.data);

export const deleteDoctor = (id) =>
  client.delete(`/api/admin/deletedoctors/${id}`).then((r) => r.data);

// ---- Medical staff ----
export const registerMedical = (payload) =>
  client.post("/api/admin/registerMedical", payload).then((r) => r.data);

export const getAllMedical = () =>
  client.get("/api/admin/getAllMedical").then((r) => r.data);

export const getMedicalById = (id) =>
  client.get(`/api/admin/getMedicalById/${id}`).then((r) => r.data);

export const updateMedical = (id, payload) =>
  client.put(`/api/admin/updateMedical/${id}`, payload).then((r) => r.data);

export const deleteMedical = (id) =>
  client.delete(`/api/admin/deleteMedical/${id}`).then((r) => r.data);
