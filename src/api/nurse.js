import client from "./client";

export const getAllPatients = () =>
  client.get("/api/nurse/allPatients").then((r) => r.data);

export const getPatientById = (id) =>
  client.get(`/api/nurse/getPatientById/${id}`).then((r) => r.data);

export const updateTreatment = (id, payload) =>
  client.put(`/api/nurse/updateTreatment/${id}`, payload).then((r) => r.data);

export const updateStatus = (id, payload) =>
  client.put(`/api/nurse/updateStatus/${id}`, payload).then((r) => r.data);

export const createReceipt = (patientId) =>
  client.post(`/api/nurse/createReceipt/${patientId}`).then((r) => r.data);

export const addMedicine = (patientId, payload) =>
  client.post(`/api/nurse/addMedicine/${patientId}`, payload).then((r) => r.data);
