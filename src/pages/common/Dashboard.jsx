import {useEffect,useState} from "react";
import {useAuth} from "../../context/AuthContext";
import {useToast} from "../../context/ToastContext";
import {dashboardApi,appointmentApi,billingApi,labApi,ipdApi} from "../../api/hmsModules";
import {PageHeader,StatCard,FullPageLoader} from "../../components/Atoms";

export default function Dashboard(){
  const {user}=useAuth(); const toast=useToast(); const [d,setD]=useState(null); const [loading,setLoading]=useState(true);
  useEffect(()=>{(async()=>{
    try{
      if(user.role==="ADMIN") setD(await dashboardApi.admin());
      else if(user.role==="DOCTOR") setD(await dashboardApi.doctor(user.profileId));
      else {
        const [a,b,l]=await Promise.allSettled([
          user.role==="PATIENT"?appointmentApi.patient(user.profileId):appointmentApi.all(),
          user.role==="PATIENT"?billingApi.list({patientId:user.profileId}):billingApi.list(),
          user.role==="PATIENT"?labApi.patient(user.profileId):labApi.all()
        ]);
        setD({todayAppointments:a.status==="fulfilled"?(a.value||[]).filter(x=>x.appointmentDate===new Date().toISOString().slice(0,10)).length:0,
          totalAppointments:a.status==="fulfilled"?(a.value||[]).length:0,
          pendingBills:b.status==="fulfilled"?(b.value||[]).filter(x=>x.status!=="PAID").length:0,
          pendingLabTests:l.status==="fulfilled"?(l.value||[]).filter(x=>x.status!=="COMPLETED"&&x.status!=="CANCELLED").length:0});
      }
    }catch(e){toast.error(e.friendlyMessage||"Could not load dashboard")}
    finally{setLoading(false)}
  })()},[user]);
  if(loading)return <FullPageLoader label="Loading dashboard…"/>;
  const cards=user.role==="ADMIN"
    ? [["Patients",d.totalPatients,"🧑‍⚕️"],["Today appointments",d.todayAppointments,"📅"],["Revenue MTD",`₹${Number(d.totalRevenueMTD||0).toLocaleString()}`,"₹"],["Beds",`${d.occupiedBeds||0}/${d.totalBeds||0}`,"🛏️"],["Occupancy",`${Number(d.bedOccupancyPercent||0).toFixed(1)}%`,"◉"],["Active admissions",d.activeAdmissions,"🏥"]]
    : user.role==="DOCTOR"
    ? [["Today's appointments",d.todayAppointments,"📅"],["Pending appointments",d.pendingAppointments,"⏳"]]
    : [["Today's appointments",d.todayAppointments,"📅"],["Total appointments",d.totalAppointments,"🗓️"],["Pending bills",d.pendingBills,"₹"],["Pending lab tests",d.pendingLabTests,"🧪"]];
  return <div><PageHeader eyebrow={user.role} title="Hospital Dashboard" subtitle={`Welcome, ${user.name}. Here is your role-specific overview.`}/>
    <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-4">{cards.map(([label,value,icon],i)=><StatCard key={label} label={label} value={value??0} icon={icon} accent={i%2?"ink":"clover"}/>)}</div>
    <div className="card p-5 mt-6"><h2 className="font-semibold">Quick guidance</h2><p className="text-sm text-ink-500 mt-2">Use the sidebar to manage only the modules allowed for your account. All requests automatically include your JWT token.</p></div>
  </div>;
}