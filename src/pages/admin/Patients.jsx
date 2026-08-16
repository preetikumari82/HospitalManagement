import {useEffect,useState} from "react";
import {getPatients,updatePatient,admitPatient,dischargePatient} 
from "../../api/patient";
import {getAllDoctors} from "../../api/admin";
import {useToast} from "../../context/ToastContext";

const blank={name:"",email:"",password:"",age:1,gender:"",phone:"",address:"",fees:0,disease:"",
 doctorId:"",treatment:"",nurseRemarks:"",status:"REFERRED",encounterType:"OPD",bedNumber:"",
 emergencyContactName:"",emergencyContactPhone:"",emergencyContactRelationship:"",
 allergies:"",pastConditions:"",prescriptions:""};

export default function Patients(){
 const [rows,setRows]=useState([]),[q,setQ]=useState(""),[status,setStatus]=useState(""),[selected,setSelected]=useState(null),[form,setForm]=useState(blank),[doctors,setDoctors]=useState([]);
 const toast=useToast();
 const load=async()=>{try{setRows(await getPatients({q,status}));}catch(e){toast.error(e?.friendlyMessage||"Could not load patients")}};
 useEffect(()=>{load();getAllDoctors().then(setDoctors).catch(()=>{});},[]);
 const edit=p=>{setSelected(p.id);setForm({...blank,...p,doctorId:p.doctorId||"",age:p.age||1});};
 const change=e=>setForm({...form,[e.target.name]:e.target.value});
 const save=async()=>{try{await updatePatient(selected,{...form,age:Number(form.age),fees:Number(form.fees),doctorId:form.doctorId?Number(form.doctorId):null});toast.success("Patient updated");load();}catch(e){toast.error(e?.friendlyMessage||"Update failed")}};
 const admit=async()=>{try{await admitPatient(selected,{...form,age:Number(form.age),fees:Number(form.fees),doctorId:form.doctorId?Number(form.doctorId):null,encounterType:"IPD",status:"ADMITTED"});toast.success("Patient admitted");load();}catch(e){toast.error(e?.friendlyMessage||"Admission failed")}};
 const discharge=async id=>{try{await dischargePatient(id);toast.success("Patient discharged");load();}catch(e){toast.error(e?.friendlyMessage||"Discharge failed")}};
 return <div>
  <div className="flex flex-wrap justify-between gap-3 mb-5"><div><h1 className="font-display text-2xl font-semibold">Patient Management</h1><p className="text-sm text-ink-500">OPD/IPD, medical history, emergency contact and bed workflow.</p></div></div>
  <div className="bg-white border border-line rounded-xl p-4 mb-5 flex flex-wrap gap-3">
   <input className="input max-w-sm" placeholder="Search name, ID or phone" value={q} onChange={e=>setQ(e.target.value)} onKeyDown={e=>e.key==="Enter"&&load()}/>
   <select className="input max-w-xs" value={status} onChange={e=>{setStatus(e.target.value);setTimeout(load,0)}}><option value="">All statuses</option><option>ADMITTED</option><option>DISCHARGED</option><option>REFERRED</option></select>
   <button className="btn-primary" onClick={load}>Search</button>
  </div>
  <div className="bg-white border border-line rounded-xl overflow-auto"><table className="w-full text-sm"><thead><tr className="border-b border-line text-left"><th className="p-3">ID</th><th className="p-3">Patient</th><th className="p-3">Phone</th><th className="p-3">Type</th><th className="p-3">Status</th><th className="p-3">Bed</th><th className="p-3">Action</th></tr></thead>
   <tbody>{rows.map(p=><tr key={p.id} className="border-b border-line last:border-0"><td className="p-3">#{p.id}</td><td className="p-3 font-semibold">{p.name}<div className="text-xs text-ink-400">{p.email}</div></td><td className="p-3">{p.phone}</td><td className="p-3">{p.encounterType||"OPD"}</td><td className="p-3">{p.status||"—"}</td><td className="p-3">{p.bedNumber||"—"}</td><td className="p-3"><button className="btn-outline text-xs" onClick={()=>edit(p)}>Manage</button>{p.status==="ADMITTED"&&<button className="btn-outline text-xs ml-2" onClick={()=>discharge(p.id)}>Discharge</button>}</td></tr>)}</tbody>
  </table></div>
  {selected&&<div className="mt-5 bg-white border border-line rounded-xl p-5"><div className="flex justify-between mb-4"><h2 className="font-display text-xl font-semibold">Patient #{selected}</h2><button onClick={()=>setSelected(null)} className="btn-outline">Close</button></div>
   <div className="grid md:grid-cols-3 gap-3">{["name","email","age","gender","phone","address","fees","disease","encounterType","bedNumber","emergencyContactName","emergencyContactPhone","emergencyContactRelationship","allergies","pastConditions","prescriptions","treatment","nurseRemarks"].map(k=>
    <div key={k} className={["address","allergies","pastConditions","prescriptions","treatment","nurseRemarks"].includes(k)?"md:col-span-3":""}><label className="label">{k.replace(/([A-Z])/g," $1")}</label><input className="input" name={k} value={form[k]??""} onChange={change} /></div>)}
    <div><label className="label">Doctor</label><select className="input" name="doctorId" value={form.doctorId||""} onChange={change}><option value="">Select doctor</option>{doctors.map(d=><option key={d.doctorId} value={d.doctorId}>{d.name} — {d.specialization}</option>)}</select></div>
    <div><label className="label">Status</label><select className="input" name="status" value={form.status||""} onChange={change}><option>REFERRED</option><option>ADMITTED</option><option>DISCHARGED</option></select></div>
   </div>
   <div className="flex gap-3 mt-5"><button className="btn-primary" onClick={save}>Save Changes</button><button className="btn-outline" onClick={admit}>Admit to IPD</button></div>
  </div>}
 </div>
}
