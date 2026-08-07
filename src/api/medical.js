import client from "./client";

export const getAllPatients = () =>
  client.get("/api/medical/allPatients").then((r) => r.data);

export const getPatientById = (id) =>
  client.get(`/api/medical/getPatientById/${id}`).then((r) => r.data);

export const addMedicine = (patientId, payload) =>
  client.post(`/api/medical/addMedicine/${patientId}`, payload).then((r) => r.data);

export const updateMedicineRate = (medicineId, payload) =>
  client.put(`/api/medical/updateMedicineRate/${medicineId}`, payload).then((r) => r.data);

export const getMedicineBill = (patientId) =>
  client.get(`/api/medical/medicineBill/${patientId}`).then((r) => r.data);

export const generateReceipt = (patientId) =>
  client.post(`/api/medical/generateReceipt/${patientId}`).then((r) => r.data);
