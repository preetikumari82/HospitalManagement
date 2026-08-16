import {useEffect,useState} from "react";
import {getMyProfile} from "../../api/patient";
import {useAuth} from "../../context/AuthContext";
export default function Profile(){
 const {user}=useAuth(); const [p,setP]=useState(null);
 useEffect(()=>{if(user)getMyProfile().then(setP)},[user]);
 if(!p)return <div>Loading profile...</div>;
 return <div><h1 className="font-display text-2xl font-semibold">My Patient Profile</h1><div className="bg-white border border-line rounded-xl p-5 mt-5 grid sm:grid-cols-2 gap-4">
 {Object.entries({Name:p.name,Email:p.email,Age:p.age,Gender:p.gender,Phone:p.phone,Address:p.address,Status:p.status,"Visit Type":p.encounterType,"Bed":p.bedNumber||"—",Allergies:p.allergies||"—","Past Conditions":p.pastConditions||"—",Prescriptions:p.prescriptions||"—","Emergency Contact":`${p.emergencyContactName||"—"} ${p.emergencyContactPhone||""}`}).map(([k,v])=><div key={k}><p className="text-xs text-ink-400">{k}</p><p className="font-medium text-ink-800 mt-1">{v}</p></div>)}
 </div></div>
}
