import client from "./client";
export const registerPatient = (payload) => client.post("/api/patients/register", payload).then(r=>r.data);
export const getPatients = (params={}) => client.get("/api/patients", {params}).then(r=>r.data);
export const getPatient = (id) => client.get(`/api/patients/${id}`).then(r=>r.data);
export const getMyProfile = () => client.get("/api/patients/me").then(r=>r.data);
export const updatePatient = (id,payload) => client.put(`/api/patients/${id}`,payload).then(r=>r.data);
export const admitPatient = (id,payload) => client.post(`/api/patients/${id}/admit`,payload).then(r=>r.data);
export const dischargePatient = (id) => client.post(`/api/patients/${id}/discharge`,{}).then(r=>r.data);
