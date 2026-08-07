import { useEffect, useState } from "react";
import * as doctorApi from "../../api/doctor";
import DataTable from "../../components/DataTable";
import Modal from "../../components/Modal";
import ConfirmDialog from "../../components/ConfirmDialog";
import { PageHeader, FormRow, ErrorText, FullPageLoader, StatCard, StatusBadge } from "../../components/Atoms";
import { useToast } from "../../context/ToastContext";
import { useAuth } from "../../context/AuthContext";

const STATUSES = ["ADMITTED", "UNDER_TREATMENT", "DISCHARGED", "REFERRED"];

const emptyForm = {
  name: "",
  email: "",
  password: "",
  age: "",
  gender: "Male",
  phone: "",
  address: "",
  fees: "",
  disease: "",
  doctorId: "",
};

export default function DoctorPatients() {
  const toast = useToast();
  const { user } = useAuth();
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [admitOpen, setAdmitOpen] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState("");

  const [viewing, setViewing] = useState(null);
  const [editOpen, setEditOpen] = useState(false);
  const [editForm, setEditForm] = useState(null);
  const [editError, setEditError] = useState("");

  const [dischargeTarget, setDischargeTarget] = useState(null);
  const [discharging, setDischarging] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const data = await doctorApi.getAllPatients();
      setPatients(data || []);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not load patients.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openAdmit = () => {
    setForm({ ...emptyForm, doctorId: user?.profileId ? String(user.profileId) : "" });
    setFormError("");
    setAdmitOpen(true);
  };

  const onAdmit = async (e) => {
    e.preventDefault();
    setFormError("");
    if (!form.name || !form.email || !form.password || !form.doctorId) {
      setFormError("Name, email, password and doctor id are required.");
      return;
    }
    setSaving(true);
    try {
      await doctorApi.admitPatient({
        ...form,
        age: Number(form.age) || 0,
        fees: Number(form.fees) || 0,
        doctorId: Number(form.doctorId),
      });
      toast.success("Patient admitted.");
      setAdmitOpen(false);
      load();
    } catch (err) {
      setFormError(err.friendlyMessage || "Could not admit patient.");
    } finally {
      setSaving(false);
    }
  };

  const openEdit = async (row) => {
    setEditError("");
    setEditForm({
      id: row.id,
      age: row.age ?? "",
      gender: row.gender || "",
      phone: row.phone || "",
      address: row.address || "",
      fees: row.fees ?? "",
      disease: row.disease || "",
      doctorId: "",
    });
    setEditOpen(true);
  };

  const onSaveEdit = async (e) => {
    e.preventDefault();
    setEditError("");
    setSaving(true);
    try {
      await doctorApi.updatePatient(editForm.id, {
        ...editForm,
        age: Number(editForm.age) || 0,
        fees: Number(editForm.fees) || 0,
        doctorId: editForm.doctorId ? Number(editForm.doctorId) : undefined,
      });
      toast.success("Patient updated.");
      setEditOpen(false);
      load();
    } catch (err) {
      setEditError(err.friendlyMessage || "Could not update patient.");
    } finally {
      setSaving(false);
    }
  };

  const onDischarge = async () => {
    if (!dischargeTarget) return;
    setDischarging(true);
    try {
      await doctorApi.dischargePatient(dischargeTarget.id);
      toast.success("Patient discharged.");
      setDischargeTarget(null);
      load();
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not discharge patient.");
    } finally {
      setDischarging(false);
    }
  };

  if (loading) return <FullPageLoader label="Loading patients…" />;

  return (
    <div>
      <PageHeader
        eyebrow="Doctor"
        title="Patients"
        subtitle="Admit new patients, review their record, and discharge when treatment is complete."
        action={
          <button className="btn-primary" onClick={openAdmit}>
            + Admit patient
          </button>
        }
      />

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
        <StatCard label="Total patients" value={patients.length} icon="🛏️" />
        {STATUSES.map((s) => (
          <StatCard key={s} label={s.replaceAll("_", " ")} value={patients.filter((p) => p.status === s).length} icon="•" accent={s === "DISCHARGED" ? "ink" : "clover"} />
        ))}
      </div>

      <DataTable
        data={patients}
        rowKey={(r) => r.id}
        searchKeys={["name", "email", "phone", "disease"]}
        searchPlaceholder="Search patients by name, email, phone, disease…"
        filters={[{ key: "status", label: "Status", options: STATUSES.map((s) => ({ value: s, label: s.replaceAll("_", " ") })) }]}
        columns={[
          { key: "id", label: "ID", width: "60px" },
          { key: "name", label: "Name" },
          { key: "disease", label: "Disease" },
          { key: "phone", label: "Phone" },
          { key: "age", label: "Age", width: "70px" },
          { key: "status", label: "Status", render: (r) => <StatusBadge status={r.status} /> },
          { key: "fees", label: "Fees", render: (r) => `₹${Number(r.fees || 0).toLocaleString()}` },
          {
            key: "actions",
            label: "Actions",
            sortable: false,
            render: (r) => (
              <div className="flex gap-2 flex-wrap">
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => setViewing(r)}>
                  View
                </button>
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => openEdit(r)}>
                  Edit
                </button>
                {r.status !== "DISCHARGED" && (
                  <button className="btn-danger px-2.5 py-1 text-xs" onClick={() => setDischargeTarget(r)}>
                    Discharge
                  </button>
                )}
              </div>
            ),
          },
        ]}
      />

      {/* Admit modal */}
      <Modal open={admitOpen} onClose={() => setAdmitOpen(false)} title="Admit patient" subtitle="Creates a login account with the PATIENT role.">
        <form onSubmit={onAdmit} className="space-y-4">
          <FormRow>
            <div>
              <label className="label">Full name</label>
              <input className="input" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div>
              <label className="label">Email</label>
              <input type="email" className="input" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
            </div>
          </FormRow>
          <FormRow>
            <div>
              <label className="label">Password</label>
              <input type="password" className="input" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
            </div>
            <div>
              <label className="label">Phone (10 digits)</label>
              <input className="input" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} placeholder="98xxxxxxxx" required />
            </div>
          </FormRow>
          <FormRow cols={3}>
            <div>
              <label className="label">Age</label>
              <input type="number" min="1" max="120" className="input" value={form.age} onChange={(e) => setForm({ ...form, age: e.target.value })} required />
            </div>
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
                <option>Male</option>
                <option>Female</option>
                <option>Other</option>
              </select>
            </div>
            <div>
              <label className="label">Fees (₹)</label>
              <input type="number" min="1" className="input" value={form.fees} onChange={(e) => setForm({ ...form, fees: e.target.value })} required />
            </div>
          </FormRow>
          <div>
            <label className="label">Address</label>
            <input className="input" value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} required />
          </div>
          <FormRow>
            <div>
              <label className="label">Disease / condition</label>
              <input className="input" value={form.disease} onChange={(e) => setForm({ ...form, disease: e.target.value })} required />
            </div>
            <div>
              <label className="label">Attending doctor ID</label>
              <input type="number" className="input" value={form.doctorId} onChange={(e) => setForm({ ...form, doctorId: e.target.value })} placeholder="e.g. 1" required />
              {user?.profileId && <p className="text-[11px] text-ink-400 mt-1">Pre-filled with your doctor ID ({user.profileId}). Change it to refer to a colleague instead.</p>}
            </div>
          </FormRow>

          <ErrorText>{formError}</ErrorText>

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn-outline" onClick={() => setAdmitOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Admitting…" : "Admit patient"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Edit modal */}
      <Modal open={editOpen} onClose={() => setEditOpen(false)} title="Update patient details" width="max-w-lg">
        {editForm && (
          <form onSubmit={onSaveEdit} className="space-y-4">
            <FormRow cols={3}>
              <div>
                <label className="label">Age</label>
                <input type="number" className="input" value={editForm.age} onChange={(e) => setEditForm({ ...editForm, age: e.target.value })} />
              </div>
              <div>
                <label className="label">Gender</label>
                <select className="input" value={editForm.gender} onChange={(e) => setEditForm({ ...editForm, gender: e.target.value })}>
                  <option>Male</option>
                  <option>Female</option>
                  <option>Other</option>
                </select>
              </div>
              <div>
                <label className="label">Fees (₹)</label>
                <input type="number" className="input" value={editForm.fees} onChange={(e) => setEditForm({ ...editForm, fees: e.target.value })} />
              </div>
            </FormRow>
            <FormRow>
              <div>
                <label className="label">Phone</label>
                <input className="input" value={editForm.phone} onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })} />
              </div>
              <div>
                <label className="label">Disease</label>
                <input className="input" value={editForm.disease} onChange={(e) => setEditForm({ ...editForm, disease: e.target.value })} />
              </div>
            </FormRow>
            <div>
              <label className="label">Address</label>
              <input className="input" value={editForm.address} onChange={(e) => setEditForm({ ...editForm, address: e.target.value })} />
            </div>

            <ErrorText>{editError}</ErrorText>

            <div className="flex justify-end gap-2 pt-2">
              <button type="button" className="btn-outline" onClick={() => setEditOpen(false)}>
                Cancel
              </button>
              <button type="submit" className="btn-primary" disabled={saving}>
                {saving ? "Saving…" : "Save changes"}
              </button>
            </div>
          </form>
        )}
      </Modal>

      {/* View modal */}
      <Modal open={!!viewing} onClose={() => setViewing(null)} title={viewing?.name} subtitle={viewing?.email} width="max-w-lg">
        {viewing && (
          <div className="grid grid-cols-2 gap-4 text-sm">
            <Field label="Status"><StatusBadge status={viewing.status} /></Field>
            <Field label="Disease" value={viewing.disease} />
            <Field label="Age" value={viewing.age} />
            <Field label="Gender" value={viewing.gender} />
            <Field label="Phone" value={viewing.phone} />
            <Field label="Fees" value={`₹${Number(viewing.fees || 0).toLocaleString()}`} />
            <Field label="Address" value={viewing.address} span2 />
            <Field label="Treatment" value={viewing.treatment || "—"} span2 />
            <Field label="Medicine" value={viewing.medicine || "—"} span2 />
            <Field label="Nurse remarks" value={viewing.nurseRemarks || "—"} span2 />
          </div>
        )}
      </Modal>

      <ConfirmDialog
        open={!!dischargeTarget}
        onClose={() => setDischargeTarget(null)}
        onConfirm={onDischarge}
        loading={discharging}
        danger={false}
        title="Discharge patient"
        message={`Mark ${dischargeTarget?.name} as discharged today?`}
        confirmLabel="Discharge"
      />
    </div>
  );
}

function Field({ label, value, children, span2 }) {
  return (
    <div className={span2 ? "col-span-2" : ""}>
      <p className="text-[11px] font-bold uppercase tracking-wide text-ink-400 mb-0.5">{label}</p>
      <div className="text-ink-800 font-medium">{children ?? value}</div>
    </div>
  );
}
