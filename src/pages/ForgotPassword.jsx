import {useState} from "react"; import {Link} from "react-router-dom"; import client from "../api/client"; import {useToast} from "../context/ToastContext";
export default function ForgotPassword(){
 const [email,setEmail]=useState(""); const [otp,setOtp]=useState(""); const [pw,setPw]=useState(""); const [sent,setSent]=useState(false); const toast=useToast();
 const send=async e=>{e.preventDefault();try{await client.post("/api/auth/forgot-password",{email});setSent(true);toast.success("OTP sent to your email.");}catch(err){toast.error(err?.friendlyMessage||"Unable to send OTP");}};
 const reset=async e=>{e.preventDefault();try{await client.post("/api/auth/reset-password",{email,otp,newPassword:pw});toast.success("Password reset successfully.");setTimeout(()=>location.href="/login",500);}catch(err){toast.error(err?.friendlyMessage||"Invalid OTP");}};
 return <div className="min-h-screen grid place-items-center bg-paper p-6"><div className="w-full max-w-md bg-white border border-line rounded-2xl p-6">
  <h1 className="font-display text-2xl font-semibold">Reset Password</h1><p className="text-sm text-ink-500 mt-1 mb-6">We will email a one-time password.</p>
  <form onSubmit={sent?reset:send} className="space-y-4"><div><label className="label">Email</label><input className="input" type="email" value={email} onChange={e=>setEmail(e.target.value)} required/></div>
  {sent&&<><div><label className="label">Email OTP</label><input className="input" value={otp} onChange={e=>setOtp(e.target.value)} required/></div><div><label className="label">New password</label><input className="input" type="password" minLength="6" value={pw} onChange={e=>setPw(e.target.value)} required/></div></>}
  <button className="btn-primary w-full">{sent?"Reset Password":"Send OTP"}</button></form>
  <Link className="block text-center mt-4 text-sm text-clover-700" to="/login">Back to login</Link>
 </div></div>
}
