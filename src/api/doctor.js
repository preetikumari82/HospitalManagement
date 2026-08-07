import client from "./client";

// ---- Patients ----
export const admitPatient = (payload) =>
  client.post("/api/doctor/admitPatient", payload).then((r) => r.data);

export const getAllPatients = () =>
  client.get("/api/doctor/allpatients").then((r) => r.data);

export const getPatientById = (id) =>
  client.get(`/api/doctor/getBypatientid/${id}`).then((r) => r.data);

export const updatePatient = (id, payload) =>
  client.put(`/api/doctor/updatePatientByid/${id}`, payload).then((r) => r.data);

export const dischargePatient = (id) =>
  client.put(`/api/doctor/patientdischargeByid/${id}`).then((r) => r.data);

// ---- Nurses ----
export const createNurse = (payload) =>
  client.post("/api/doctor/createNurse", payload).then((r) => r.data);

export const getAllNurses = () =>
  client.get("/api/doctor/getAllNurses").then((r) => r.data);

export const getNurseById = (id) =>
  client.get(`/api/doctor/getNurseById/${id}`).then((r) => r.data);

export const updateNurse = (id, payload) =>
  client.put(`/api/doctor/updateNurse/${id}`, payload).then((r) => r.data);

export const deleteNurse = (id) =>
  client.delete(`/api/doctor/deleteNurse/${id}`).then((r) => r.data);
