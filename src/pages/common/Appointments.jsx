import {useEffect,useState} from "react";
import {useAuth} from "../../context/AuthContext"; import {useToast} from "../../context/ToastContext";
import {appointmentApi} from "../../api/hmsModules"; import {PageHeader,FullPageLoader} from "../../components/Atoms";
const STATUSES=["PENDING","CONFIRMED","COMPLETED","CANCELLED"];
const canCreate=r=>["PATIENT","RECEPTIONIST","ADMIN"].includes(r);
const canStatus=r=>["RECEPTIONIST","ADMIN","DOCTOR"].includes(r);
export default function Appointments(){
 const {user}=useAuth(); const toast=useToast(); const [rows,setRows]=useState([]); const [loading,setLoading]=useState(true);
 const [f,setF]=useState({patientId:user?.role==="PATIENT"?String(user.profileId):"",doctorId:"",appointmentDate:"",timeSlot:"",reason:""});
 const load=async()=>{setLoading(true);try{const d=user.role==="DOCTOR"?await appointmentApi.doctor(user.profileId):user.role==="PATIENT"?await appointmentApi.patient(user.profileId):await appointmentApi.all();setRows(d||[])}catch(e){toast.error(e.friendlyMessage||"Could not load appointments")}finally{setLoading(false)}};
 useEffect(()=>{load()},[user]);
 const save=async e=>{e.preventDefault();try{await appointmentApi.create({...f,patientId:Number(f.patientId),doctorId:Number(f.doctorId)});toast.success("Appointment booked");setF({...f,doctorId:"",appointmentDate:"",timeSlot:"",reason:""});load()}catch(e){toast.error(e.friendlyMessage||"Booking failed")}};
 const changeStatus=async(id,status)=>{try{await appointmentApi.status(id,status);toast.success(`Status changed to ${status}`);load()}catch(e){toast.error(e.friendlyMessage||"Could not change status")}};
 const cancel=async id=>{try{await appointmentApi.cancel(id);toast.success("Appointment cancelled");load()}catch(e){toast.error(e.friendlyMessage||"Could not cancel appointment")}};
 if(loading)return <FullPageLoader label="Loading appointments…"/>;
 return <div><PageHeader eyebrow="FR4" title="Appointments" subtitle="Book, reschedule and manage appointment status."/>
 {canCreate(user.role)&&<form onSubmit={save} className="card p-5 mb-6 grid md:grid-cols-5 gap-3">
   {user.role!=="PATIENT"&&<input className="input" placeholder="Patient ID" value={f.patientId} onChange={e=>setF({...f,patientId:e.target.value})} required/>}
   <input className="input" placeholder="Doctor ID" value={f.doctorId} onChange={e=>setF({...f,doctorId:e.target.value})} required/>
   <input className="input" type="date" value={f.appointmentDate} onChange={e=>setF({...f,appointmentDate:e.target.value})} required/>
   <input className="input" type="time" value={f.timeSlot} onChange={e=>setF({...f,timeSlot:e.target.value})} required/>
   <button className="btn-primary">Book appointment</button>
   <input className="input md:col-span-5" placeholder="Reason" value={f.reason} onChange={e=>setF({...f,reason:e.target.value})}/>
 </form>}
 <div className="card overflow-x-auto"><table className="w-full text-sm"><thead><tr className="bg-ink-50"><th className="th">ID</th><th className="th">Date</th><th className="th">Time</th><th className="th">Patient</th><th className="th">Doctor</th><th className="th">Status</th><th className="th">Action</th></tr></thead>
 <tbody>{rows.map(r=><tr key={r.id} className="border-t border-line"><td className="td">{r.id}</td><td className="td">{r.appointmentDate}</td><td className="td">{r.timeSlot}</td><td className="td">{r.patientName}</td><td className="td">{r.doctorName}</td><td className="td"><span className="badge bg-ink-100">{r.status}</span></td><td className="td"><div className="flex gap-2 flex-wrap">
 {canStatus(user.role)&&r.status!=="CANCELLED"&&<select className="input !w-auto !py-1" value={r.status} onChange={e=>changeStatus(r.id,e.target.value)}>{STATUSES.map(s=><option key={s}>{s}</option>)}</select>}
 {["PATIENT","RECEPTIONIST","ADMIN"].includes(user.role)&&r.status!=="CANCELLED"&&<button className="btn-danger px-2 py-1 text-xs" onClick={()=>cancel(r.id)}>Cancel</button>}
 </div></td></tr>)}</tbody></table>{!rows.length&&<p className="p-6 text-sm text-ink-400">No appointments found.</p>}</div></div>;
}