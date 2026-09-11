import {useEffect,useState} from 'react'; import {scheduleApi} from '../../api/hmsModules'; import {useAuth} from '../../context/AuthContext'; import {PageHeader,FormRow,FullPageLoader} from '../../components/Atoms'; import {useToast} from '../../context/ToastContext';
const days=['MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'];
export default function DoctorSchedule(){
  const {user}=useAuth();
  const toast=useToast();
  const [rows,setRows]=useState([]);
  const [f,setF]=useState({dayOfWeek:'MONDAY',startTime:'09:00',endTime:'13:00',slotMinutes:30});
  const [loading,setLoading]=useState(true);
  const load=()=>scheduleApi.get(user.profileId).then(setRows).catch(e=>toast.error(e.friendlyMessage||'Could not load schedule')).finally(()=>setLoading(false));
  useEffect(()=>{load()},[]);
  const save=async e=>{
    e.preventDefault();
    try{
      await scheduleApi.create({...f,doctorId:user.profileId,slotMinutes:Number(f.slotMinutes)});
      toast.success('Schedule slot added');
      load()
    }catch(e){
      toast.error(e.friendlyMessage||'Could not save schedule')
    }
  };
  if(loading) return <FullPageLoader label="Loading schedule" />;
  return (
    <div>
      <PageHeader eyebrow="FR3" title="Doctor availability" subtitle="Weekly slots and leave-ready availability." />
      <div className="grid lg:grid-cols-3 gap-6">
        <form onSubmit={save} className="card p-5 space-y-4">
          <h3 className="font-semibold">Add weekly slot</h3>
          <select className="input" value={f.dayOfWeek} onChange={e=>setF({...f,dayOfWeek:e.target.value})}>
            {days.map(d=><option key={d}>{d}</option>)}
          </select>
          <FormRow>
            <input className="input" type="time" value={f.startTime} onChange={e=>setF({...f,startTime:e.target.value})}/>
            <input className="input" type="time" value={f.endTime} onChange={e=>setF({...f,endTime:e.target.value})}/>
          </FormRow>
          <input className="input" type="number" min="5" value={f.slotMinutes} onChange={e=>setF({...f,slotMinutes:e.target.value})}/>
          <button className="btn-primary w-full">Save slot</button>
        </form>
        <div className="lg:col-span-2 card overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-ink-50">
                <th className="p-3 text-left">Day</th>
                <th className="p-3 text-left">Time</th>
                <th className="p-3">Slot</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {rows.map(r=>
                <tr key={r.id} className="border-t">
                  <td className="p-3">{r.dayOfWeek}</td>
                  <td className="p-3">{r.startTime} - {r.endTime}</td>
                  <td className="p-3 text-center">{r.slotMinutes} min</td>
                  <td className="p-3 text-right">
                    <button className="btn-danger px-3 py-1 text-xs" onClick={async()=>{await scheduleApi.remove(r.id);load()}}>Delete</button>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}