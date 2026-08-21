import {BrowserRouter,Routes,Route,Navigate} from "react-router-dom";
import {AuthProvider,useAuth} from "./context/AuthContext"; import {ToastProvider} from "./context/ToastContext"; import ProtectedRoute from "./components/ProtectedRoute"; import Layout from "./components/Layout";
import Login from "./pages/Login"; import PatientRegister from "./pages/PatientRegister"; import ForgotPassword from "./pages/ForgotPassword";
import Dashboard from "./pages/common/Dashboard"; import Appointments from "./pages/common/Appointments"; import LabTests from "./pages/common/LabTests"; import Bills from "./pages/common/Bills";
import Staff from "./pages/admin/Staff"; import AdminPatients from "./pages/admin/Patients"; import Doctors from "./pages/admin/Doctors"; import MedicalStaff from "./pages/admin/MedicalStaff"; import Departments from "./pages/admin/Departments";
import DoctorPatients from "./pages/doctor/Patients"; import DoctorNurses from "./pages/doctor/Nurses"; import NursePatients from "./pages/nurse/Patients"; import MedicalPatients from "./pages/medical/Patients"; import Profile from "./pages/patient/Profile";
import EHR from "./pages/modules/EHR"; import DoctorSchedule from "./pages/modules/DoctorSchedule"; import DoctorLeave from "./pages/modules/DoctorLeave"; import IPD from "./pages/modules/IPD"; import MedicineStock from "./pages/modules/MedicineStock";
function home(role){return role==="PATIENT"?"/patient/profile":"/dashboard"}
function Root(){const {user,token}=useAuth();return token&&user?<Navigate to={home(user.role)} replace/>:<Navigate to="/login" replace/>}
const R=({roles,children})=><ProtectedRoute roles={roles}>{children}</ProtectedRoute>;
export default function App(){return <BrowserRouter><ToastProvider><AuthProvider><Routes>
 <Route path="/login" element={<Login/>}/><Route path="/register" element={<PatientRegister/>}/><Route path="/forgot-password" element={<ForgotPassword/>}/>
 <Route element={<ProtectedRoute><Layout/></ProtectedRoute>}>
  <Route path="/dashboard" element={<Dashboard/>}/><Route path="/appointments" element={<Appointments/>}/><Route path="/lab-tests" element={<LabTests/>}/><Route path="/bills" element={<Bills/>}/><Route path="/ehr" element={<EHR/>}/><Route path="/ipd" element={<IPD/>}/>
  <Route path="/admin/doctors" element={<R roles={["ADMIN"]}><Doctors/></R>}/><Route path="/admin/departments" element={<R roles={["ADMIN"]}><Departments/></R>}/><Route path="/admin/staff" element={<R roles={["ADMIN"]}><Staff/></R>}/><Route path="/admin/medical" element={<R roles={["ADMIN"]}><MedicalStaff/></R>}/><Route path="/admin/patients" element={<R roles={["ADMIN","RECEPTIONIST","DOCTOR","NURSE"]}><AdminPatients/></R>}/>
  <Route path="/doctor/patients" element={<R roles={["DOCTOR"]}><DoctorPatients/></R>}/><Route path="/doctor/nurses" element={<R roles={["DOCTOR"]}><DoctorNurses/></R>}/><Route path="/doctor/schedule" element={<R roles={["ADMIN","DOCTOR","PATIENT"]}><DoctorSchedule/></R>}/><Route path="/doctor/leave" element={<R roles={["ADMIN","DOCTOR"]}><DoctorLeave/></R>}/>
  <Route path="/nurse/patients" element={<R roles={["NURSE"]}><NursePatients/></R>}/><Route path="/medical/patients" element={<R roles={["MEDICAL","PHARMACIST"]}><MedicalPatients/></R>}/><Route path="/medicine-stock" element={<R roles={["ADMIN","MEDICAL","PHARMACIST"]}><MedicineStock/></R>}/><Route path="/patient/profile" element={<R roles={["PATIENT"]}><Profile/></R>}/>
 </Route>
 <Route path="/" element={<Root/>}/><Route path="*" element={<Root/>}/>
 </Routes></AuthProvider></ToastProvider></BrowserRouter>}
