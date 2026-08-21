import {useEffect,useState} from "react";
import {useAuth} from "../../context/AuthContext"; import {useToast} from "../../context/ToastContext"; import {billingApi} from "../../api/hmsModules";
import {PageHeader,FullPageLoader} from "../../components/Atoms";
const TYPES=["CONSULTATION","LAB_TEST","MEDICINE","ROOM_CHARGE","OTHER"];
export default function Bills(){
 const {user}=useAuth(); const toast=useToast(); const [rows,setRows]=useState([]); const [loading,setLoading]=useState(true);
 const [f,setF]=useState({patientId:user.role==="PATIENT"?String(user.profileId):"",appointmentId:"",itemType:"CONSULTATION",description:"Consultation",amount:"",quantity:1});
 const canCreate=["RECEPTIONIST","ADMIN","MEDICAL"].includes(user.role);
 const canPay=["RECEPTIONIST","ADMIN","MEDICAL","PATIENT"].includes(user.role);
 const load=async()=>{setLoading(true);try{setRows(await billingApi.list(user.role==="PATIENT"?{patientId:user.profileId}:{}))}catch(e){toast.error(e.friendlyMessage||"Could not load bills")}finally{setLoading(false)}};
 useEffect(()=>{load()},[user]);
 const create=async e=>{e.preventDefault();try{await billingApi.create({patientId:Number(f.patientId),appointmentId:f.appointmentId?Number(f.appointmentId):null,items:[{itemType:f.itemType,description:f.description,amount:Number(f.amount),quantity:Number(f.quantity)||1}]});toast.success("Bill created");setF({...f,appointmentId:"",description:"Consultation",amount:"",quantity:1});load()}catch(e){toast.error(e.friendlyMessage||"Could not create bill")}};
 const pay=async id=>{const raw=window.prompt("Payment amount (blank = full balance)"); if(raw===null)return; try{await billingApi.pay(id,raw.trim()===""?null:Number(raw));toast.success("Payment recorded");load()}catch(e){toast.error(e.friendlyMessage||"Payment failed")}};
 const pdf=async id=>{try{const blob=await billingApi.invoice(id);const url=URL.createObjectURL(blob);const a=document.createElement("a");a.href=url;a.download=`invoice-${id}.pdf`;a.click();setTimeout(()=>URL.revokeObjectURL(url),1000)}catch(e){toast.error(e.friendlyMessage||"Could not generate invoice")}};
 if(loading)return <FullPageLoader label="Loading bills…"/>;
 return <div><PageHeader eyebrow="FR6" title="Billing & Payments" subtitle="Itemized bills, payments and PDF invoices."/>
 {canCreate&&<form onSubmit={create} className="card p-5 mb-6 grid md:grid-cols-6 gap-3">
   <input className="input" placeholder="Patient ID" value={f.patientId} onChange={e=>setF({...f,patientId:e.target.value})} required/>
   <input className="input" placeholder="Appointment ID" value={f.appointmentId} onChange={e=>setF({...f,appointmentId:e.target.value})}/>
   <select className="input" value={f.itemType} onChange={e=>setF({...f,itemType:e.target.value})}>{TYPES.map(t=><option key={t}>{t}</option>)}</select>
   <input className="input" placeholder="Description" value={f.description} onChange={e=>setF({...f,description:e.target.value})} required/>
   <input className="input" type="number" min="0" step="0.01" placeholder="Amount" value={f.amount} onChange={e=>setF({...f,amount:e.target.value})} required/>
   <input className="input" type="number" min="1" placeholder="Qty" value={f.quantity} onChange={e=>setF({...f,quantity:e.target.value})}/>
   <button className="btn-primary md:col-span-6">Create bill</button>
 </form>}
 <div className="space-y-4">{rows.map(b=><div className="card p-5" key={b.id}><div className="flex flex-wrap justify-between gap-3"><div><h3 className="font-semibold">Invoice #{b.id}</h3><p className="text-sm text-ink-500">{b.patientName||"Patient"} · {b.createdAt}</p></div><span className="badge bg-ink-100">{b.status}</span></div>
 <div className="mt-4 divide-y">{(b.items||[]).map(i=><div className="py-2 flex justify-between text-sm" key={i.id}><span>{i.itemType} — {i.description} × {i.quantity}</span><span>₹{(Number(i.amount||0)*Number(i.quantity||1)).toFixed(2)}</span></div>)}</div>
 <div className="mt-4 flex flex-wrap gap-5 text-sm"><b>Total ₹{Number(b.totalAmount||0).toFixed(2)}</b><span>Paid ₹{Number(b.paidAmount||0).toFixed(2)}</span><span>Balance ₹{Number(b.balanceAmount||0).toFixed(2)}</span></div>
 <div className="mt-4 flex gap-2">{canPay&&b.status!=="PAID"&&<button className="btn-primary" onClick={()=>pay(b.id)}>Record payment</button>}<button className="btn-outline" onClick={()=>pdf(b.id)}>PDF invoice</button></div></div>)}{!rows.length&&<div className="card p-6 text-sm text-ink-400">No bills found.</div>}</div></div>;
}