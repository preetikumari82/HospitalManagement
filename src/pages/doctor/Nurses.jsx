import { useEffect, useState } from "react";
import * as doctorApi from "../../api/doctor";
import DataTable from "../../components/DataTable";
import Modal from "../../components/Modal";
import ConfirmDialog from "../../components/ConfirmDialog";
import { PageHeader, FormRow, ErrorText, FullPageLoader, StatCard } from "../../components/Atoms";
import { useToast } from "../../context/ToastContext";
import { useAuth } from "../../context/AuthContext";

const SHIFTS = ["Morning", "Evening", "Night"];

const buildEmptyForm = (doctorId) => ({
  name: "",
  email: "",
  password: "",
  phone: "",
  gender: "Female",
  experience: "",
  shift: SHIFTS[0],
  doctorId: doctorId ? String(doctorId) : "",
});

export default function DoctorNurses() {
  const toast = useToast();
  const { user } = useAuth();
  const [nurses, setNurses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(buildEmptyForm());
  const [formError, setFormError] = useState("");
  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const data = await doctorApi.getAllNurses();
      setNurses(data || []);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not load nurses.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const openCreate = () => {
    setEditing(null);
    setForm(buildEmptyForm(user?.profileId));
    setFormError("");
    setModalOpen(true);
  };

  const openEdit = (row) => {
    setEditing(row);
    setForm({
      name: row.name || "",
      email: row.email || "",
      password: "",
      phone: row.phone || "",
      gender: row.gender || "Female",
      experience: row.experience ?? "",
      shift: row.shift || SHIFTS[0],
      doctorId: row.doctorId ? String(row.doctorId) : "",
    });
    setFormError("");
    setModalOpen(true);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setFormError("");
    if (!form.name || !form.email || (!editing && !form.password) || !form.doctorId) {
      setFormError("Name, email, password and doctor id are required.");
      return;
    }
    setSaving(true);
    const payload = { ...form, experience: Number(form.experience) || 0, doctorId: Number(form.doctorId) };
    try {
      if (editing) {
        await doctorApi.updateNurse(editing.id, payload);
        toast.success("Nurse updated.");
      } else {
        await doctorApi.createNurse(payload);
        toast.success("Nurse created.");
      }
      setModalOpen(false);
      load();
    } catch (err) {
      setFormError(err.friendlyMessage || "Could not save nurse.");
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await doctorApi.deleteNurse(confirmTarget.id);
      toast.success("Nurse removed.");
      setConfirmTarget(null);
      load();
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not delete nurse.");
    } finally {
      setDeleting(false);
    }
  };

  if (loading) return <FullPageLoader label="Loading nurses…" />;

  return (
    <div>
      <PageHeader
        eyebrow="Doctor"
        title="Nurses"
        subtitle="Create and manage the nursing staff assigned under you."
        action={
          <button className="btn-primary" onClick={openCreate}>
            + Add nurse
          </button>
        }
      />

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
        <StatCard label="Total nurses" value={nurses.length} icon="👩‍⚕️" />
        {SHIFTS.map((s) => (
          <StatCard key={s} label={`${s} shift`} value={nurses.filter((n) => n.shift === s).length} icon="•" accent="clay" />
        ))}
      </div>

      <DataTable
        data={nurses}
        rowKey={(r) => r.id}
        searchKeys={["name", "email", "phone"]}
        searchPlaceholder="Search nurses by name, email, phone…"
        filters={[{ key: "shift", label: "Shift", options: SHIFTS.map((s) => ({ value: s, label: s })) }]}
        columns={[
          { key: "id", label: "ID", width: "60px" },
          { key: "name", label: "Name" },
          { key: "email", label: "Email" },
          { key: "phone", label: "Phone" },
          { key: "experience", label: "Exp. (yrs)", width: "100px" },
          { key: "shift", label: "Shift", render: (r) => <span className="badge bg-clay-400/20 text-clay-600">{r.shift}</span> },
          { key: "doctorId", label: "Doctor ID", width: "90px" },
          {
            key: "actions",
            label: "Actions",
            sortable: false,
            render: (r) => (
              <div className="flex gap-2">
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => openEdit(r)}>
                  Edit
                </button>
                <button className="btn-danger px-2.5 py-1 text-xs" onClick={() => setConfirmTarget(r)}>
                  Delete
                </button>
              </div>
            ),
          },
        ]}
      />

      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? "Edit nurse" : "Add nurse"}
        subtitle={editing ? `Updating ${editing.name}` : "Creates a login account with the NURSE role."}
      >
        <form onSubmit={onSubmit} className="space-y-4">
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
              <label className="label">Password {editing && <span className="normal-case font-normal text-ink-400">(leave blank to keep)</span>}</label>
              <input
                type="password"
                className="input"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required={!editing}
              />
            </div>
            <div>
              <label className="label">Phone</label>
              <input className="input" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
            </div>
          </FormRow>
          <FormRow cols={3}>
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
                <option>Male</option>
                <option>Female</option>
                <option>Other</option>
              </select>
            </div>
            <div>
              <label className="label">Experience (yrs)</label>
              <input type="number" min="0" className="input" value={form.experience} onChange={(e) => setForm({ ...form, experience: e.target.value })} />
            </div>
            <div>
              <label className="label">Shift</label>
              <select className="input" value={form.shift} onChange={(e) => setForm({ ...form, shift: e.target.value })}>
                {SHIFTS.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>
          </FormRow>
          <div>
            <label className="label">Supervising doctor ID</label>
            <input type="number" className="input" value={form.doctorId} onChange={(e) => setForm({ ...form, doctorId: e.target.value })} required />
          </div>

          <ErrorText>{formError}</ErrorText>

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn-outline" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Saving…" : editing ? "Save changes" : "Add nurse"}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!confirmTarget}
        onClose={() => setConfirmTarget(null)}
        onConfirm={onDelete}
        loading={deleting}
        title="Delete nurse"
        message={`Remove ${confirmTarget?.name} from the system? This cannot be undone.`}
        confirmLabel="Delete"
      />
    </div>
  );
}
