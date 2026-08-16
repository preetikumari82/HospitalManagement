import {useState} from "react";
import {useNavigate,Link} from "react-router-dom";
import {registerPatient} from "../api/patient";
import {useToast} from "../context/ToastContext";

const initial={name:"",email:"",password:"",age:"",gender:"",phone:"",address:"",
 emergencyContactName:"",emergencyContactPhone:"",emergencyContactRelationship:""};

export default function PatientRegister(){
 const [form,setForm]=useState(initial); const [loading,setLoading]=useState(false);
 const toast=useToast(); const nav=useNavigate();
 const change=e=>setForm({...form,[e.target.name]:e.target.value});
 const submit=async e=>{e.preventDefault();setLoading(true);
  try{await registerPatient({...form,age:Number(form.age)});toast.success("Registration successful. You can sign in now.");nav("/login");}
  catch(err){toast.error(err?.friendlyMessage||"Registration failed");} finally{setLoading(false);}
 };
 return <div className="min-h-screen bg-paper flex items-center justify-center p-6">
  <form onSubmit={submit} className="w-full max-w-2xl bg-white border border-line rounded-2xl p-6 shadow-sm">
   <h1 className="font-display text-2xl font-semibold text-ink-900">Patient Self Registration</h1>
   <p className="text-sm text-ink-500 mb-6">Create your hospital account and basic patient record.</p>
   <div className="grid sm:grid-cols-2 gap-4">
    {["name","email","password","age","gender","phone","address","emergencyContactName","emergencyContactPhone","emergencyContactRelationship"].map(k=>
      <div key={k} className={k==="address"?"sm:col-span-2":""}><label className="label">{k.replace(/([A-Z])/g," $1")}</label>
       <input className="input" name={k} value={form[k]} onChange={change} required={["name","email","password","age","gender","phone","address"].includes(k)} type={k==="password"?"password":k==="age"?"number":"text"}/>
      </div>)}
   </div>
   <button disabled={loading} className="btn-primary w-full mt-6">{loading?"Creating...":"Create Patient Account"}</button>
   <p className="text-sm text-center mt-4"><Link className="text-clover-700 font-semibold" to="/login">Back to login</Link></p>
  </form>
 </div>
}
