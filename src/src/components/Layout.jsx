import {NavLink,Outlet,useNavigate} from "react-router-dom";
import {useAuth} from "../context/AuthContext"; import {useToast} from "../context/ToastContext"; import {RoleBadge} from "./Atoms"; import {useState} from "react";
const NAV={
 ADMIN:[
  ["/dashboard","Dashboard","📊"],["/admin/doctors","Doctors","🩺"],["/admin/departments","Departments","🏢"],["/admin/staff","Staff Accounts","👥"],["/admin/medical","Medical Staff","💊"],["/admin/patients","Patients","🧑‍⚕️"],["/appointments","Appointments","📅"],["/ipd","Beds & IPD","🛏️"],["/bills","Billing","₹"],["/lab-tests","Laboratory","🧪"],["/medicine-stock","Medicine Stock","📦"],["/ehr","EHR","📋"]],
 DOCTOR:[
  ["/dashboard","Dashboard","📊"],["/doctor/patients","Patients","🧑‍⚕️"],["/appointments","Appointments","📅"],["/doctor/schedule","Schedule","🗓️"],["/doctor/leave","Leave","🏖️"],["/ehr","EHR","📋"],["/lab-tests","Laboratory","🧪"],["/ipd","Beds & IPD","🛏️"]],
 NURSE:[
  ["/dashboard","Dashboard","📊"],["/nurse/patients","Patients","🧑‍⚕️"],["/appointments","Appointments","📅"],["/ipd","Beds & IPD","🛏️"],["/lab-tests","Laboratory","🧪"]],
 RECEPTIONIST:[
  ["/dashboard","Dashboard","📊"],["/admin/patients","Patients","🧑‍⚕️"],["/appointments","Appointments","📅"],["/ipd","Beds & IPD","🛏️"],["/bills","Billing","₹"]],
 MEDICAL:[
  ["/dashboard","Dashboard","📊"],["/medical/patients","Pharmacy Patients","💊"],["/medicine-stock","Medicine Stock","📦"],["/bills","Billing","₹"],["/lab-tests","Laboratory","🧪"]],
 PHARMACIST:[
  ["/dashboard","Dashboard","📊"],["/medical/patients","Pharmacy Patients","💊"],["/medicine-stock","Medicine Stock","📦"],["/bills","Billing","₹"]],
 LAB_TECH:[
  ["/dashboard","Dashboard","📊"],["/lab-tests","Laboratory","🧪"],["/appointments","Appointments","📅"]],
 PATIENT:[
  ["/dashboard","Dashboard","📊"],["/patient/profile","My Profile","👤"],["/appointments","Appointments","📅"],["/ehr","Medical Records","📋"],["/lab-tests","Lab Reports","🧪"],["/bills","My Bills","₹"],["/ipd","My Admissions","🛏️"]]
};
const TITLES={ADMIN:"Administrator",DOCTOR:"Doctor",NURSE:"Nurse",RECEPTIONIST:"Reception",MEDICAL:"Medical / Pharmacy",PHARMACIST:"Pharmacist",LAB_TECH:"Lab Technician",PATIENT:"Patient"};
export default function Layout(){
 const {user,logout}=useAuth();const toast=useToast();const nav= NAV[user?.role]||[];const [open,setOpen]=useState(false);const navigate=useNavigate();
 const out=async()=>{await logout();toast.info("Logged out");navigate("/login",{replace:true})};
 const Links=()=> <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">{nav.map(([to,label,icon])=><NavLink key={to} to={to} onClick={()=>setOpen(false)} className={({isActive})=>`flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium ${isActive?"bg-clover-700 text-white":"text-ink-600 hover:bg-ink-100"}`}><span>{icon}</span>{label}</NavLink>)}</nav>;
 return <div className="min-h-screen flex">
  <aside className="hidden lg:flex w-64 shrink-0 flex-col border-r border-line bg-white"><Brand/><Links/><UserBox user={user} onLogout={out}/></aside>
  {open&&<div className="fixed inset-0 z-40 lg:hidden"><div className="fixed inset-0 bg-ink-950/50" onClick={()=>setOpen(false)}/><aside className="relative z-50 flex h-full w-72 flex-col bg-white"><Brand/><Links/><UserBox user={user} onLogout={out}/></aside></div>}
  <div className="flex-1 min-w-0 flex flex-col"><header className="lg:hidden flex items-center justify-between border-b border-line bg-white px-4 py-3 sticky top-0 z-30"><button onClick={()=>setOpen(true)} className="btn-ghost px-2">☰</button><span className="font-display font-semibold">Meridian Care</span><RoleBadge role={user?.role}/></header>
  <header className="hidden lg:flex items-center justify-between border-b border-line bg-white/80 backdrop-blur px-8 py-3 sticky top-0 z-30"><p className="text-xs font-semibold text-ink-400">{TITLES[user?.role]} Dashboard</p><span className="text-sm text-ink-500">{new Date().toLocaleDateString(undefined,{weekday:"long",year:"numeric",month:"long",day:"numeric"})}</span></header>
  <main className="flex-1 p-4 sm:p-8"><Outlet/></main></div>
 </div>
}
function Brand(){return <div className="flex items-center gap-2.5 px-5 py-5 border-b border-line"><div className="h-9 w-9 rounded-lg bg-clover-700 grid place-items-center text-white font-bold">M</div><div><p className="font-display font-semibold text-ink-900">Meridian Care</p><p className="text-[11px] text-ink-400">Hospital Operations</p></div></div>}
function UserBox({user,onLogout}){return <div className="border-t border-line p-4"><div className="flex items-center gap-3 mb-3"><div className="h-9 w-9 rounded-full bg-ink-800 text-white grid place-items-center text-sm font-semibold">{(user?.name||"?")[0].toUpperCase()}</div><div className="min-w-0"><p className="text-sm font-semibold truncate">{user?.name}</p><p className="text-xs text-ink-400 truncate">{user?.email}</p></div></div><button onClick={onLogout} className="btn-outline w-full text-xs">Log out</button></div>}
