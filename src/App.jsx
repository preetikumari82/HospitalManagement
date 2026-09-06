import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { ToastProvider } from "./context/ToastContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import { roleHome } from "./utils/roleHome";
import PatientRegister from "./pages/PatientRegister";
import ForgotPassword from "./pages/ForgotPassword";
import Staff from "./pages/admin/Staff";
import AdminPatients from "./pages/admin/Patients";
import PatientProfile from "./pages/patient/Profile";

import Doctors from "./pages/admin/Doctors";
import MedicalStaff from "./pages/admin/MedicalStaff";
import DoctorPatients from "./pages/doctor/Patients";
import DoctorNurses from "./pages/doctor/Nurses";
import NursePatients from "./pages/nurse/Patients";
import MedicalPatients from "./pages/medical/Patients";
import DoctorSchedule from "./pages/modules/DoctorSchedule";
import DoctorLeave from "./pages/modules/DoctorLeave";
import Appointments from "./pages/modules/Appointments";
import EHR from "./pages/modules/EHR";
import Billing from "./pages/modules/Billing";

function RootRedirect() {
  const { user, token } = useAuth();
  if (!token || !user) return <Navigate to="/login" replace />;
  return <Navigate to={roleHome(user.role)} replace />;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<PatientRegister />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />

      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route
          path="/admin/doctors"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <Doctors />
            </ProtectedRoute>
          }
        />
        <Route path="/admin/staff" element={<ProtectedRoute roles={["ADMIN"]}><Staff /></ProtectedRoute>} />
        <Route path="/admin/patients" element={<ProtectedRoute roles={["ADMIN","RECEPTIONIST","DOCTOR","NURSE"]}><AdminPatients /></ProtectedRoute>} />
        <Route path="/patient/profile" element={<ProtectedRoute roles={["PATIENT"]}><PatientProfile /></ProtectedRoute>} />

        <Route path="/appointments" element={<ProtectedRoute roles={["ADMIN","DOCTOR","PATIENT","RECEPTIONIST"]}><Appointments /></ProtectedRoute>} />
        <Route path="/ehr" element={<ProtectedRoute roles={["ADMIN","DOCTOR","PATIENT"]}><EHR /></ProtectedRoute>} />
        <Route path="/billing" element={<ProtectedRoute roles={["ADMIN","RECEPTIONIST","MEDICAL","PATIENT"]}><Billing /></ProtectedRoute>} />
        <Route path="/doctor/schedule" element={<ProtectedRoute roles={["ADMIN","DOCTOR"]}><DoctorSchedule /></ProtectedRoute>} />
        <Route path="/doctor/leave" element={<ProtectedRoute roles={["ADMIN","DOCTOR"]}><DoctorLeave /></ProtectedRoute>} />


        <Route
          path="/admin/medical"
          element={
            <ProtectedRoute roles={["ADMIN"]}>
              <MedicalStaff />
            </ProtectedRoute>
          }
        />

        <Route
          path="/doctor/patients"
          element={
            <ProtectedRoute roles={["DOCTOR"]}>
              <DoctorPatients />
            </ProtectedRoute>
          }
        />
        <Route
          path="/doctor/nurses"
          element={
            <ProtectedRoute roles={["DOCTOR"]}>
              <DoctorNurses />
            </ProtectedRoute>
          }
        />

        <Route
          path="/nurse/patients"
          element={
            <ProtectedRoute roles={["NURSE"]}>
              <NursePatients />
            </ProtectedRoute>
          }
        />

        <Route
          path="/medical/patients"
          element={
            <ProtectedRoute roles={["MEDICAL"]}>
              <MedicalPatients />
            </ProtectedRoute>
          }
        />
      </Route>

      <Route path="/" element={<RootRedirect />} />
      <Route path="*" element={<RootRedirect />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </ToastProvider>
    </BrowserRouter>
  );
}
