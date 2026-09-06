import { useEffect, useMemo, useState } from "react";
import * as adminApi from "../../api/admin";
import DataTable from "../../components/DataTable";
import Modal from "../../components/Modal";
import ConfirmDialog from "../../components/ConfirmDialog";
import { PageHeader, FormRow, ErrorText, FullPageLoader, StatCard } from "../../components/Atoms";
import { useToast } from "../../context/ToastContext";

const SPECIALIZATIONS = ["CARDIOLOGIST", "DENTIST", "NEUROLOGIST", "GENERAL_PHYSICIAN", "ORTHOPEDIC"];

const emptyForm = { name: "", email: "", password: "", age: "", salary: "", phone: "", specialization: SPECIALIZATIONS[0], qualification: "", consultationFee: "", departmentId: "" };

export default function Doctors() {
  const toast = useToast();
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [formError, setFormError] = useState("");
  const [confirmTarget, setConfirmTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const data = await adminApi.getAllDoctors();
      setDoctors(data || []);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not load doctors.");
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

  setForm({
    name: "",
    email: "",
    password: "",
    age: "",
    salary: "",
    phone: "",
    specialization: SPECIALIZATIONS[0],


  });

  setFormError("");
  setModalOpen(true);
};


  const openEdit = (doc) => {
    setEditing(doc);
    setForm({
      name: doc.name || "",
      email: doc.email || "",
      password: "",
      age: doc.age ?? "",
      salary: doc.salary ?? "",
      phone: doc.phone || "",
      specialization: doc.specialization || SPECIALIZATIONS[0],
      qualification: doc.qualification || "", consultationFee: doc.consultationFee ?? "", departmentId: doc.departmentId ?? "",
    });
    setFormError("");
    setModalOpen(true);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setFormError("");
    if (!form.name || !form.email || (!editing && !form.password)) {
      setFormError("Name, email and password are required.");
      return;
    }
    setSaving(true);
    const payload = {
      name: form.name,
      email: form.email.toLowerCase(),
      password: form.password,
      age: Number(form.age) || 0,
      salary: Number(form.salary) || 0,
      phone: form.phone,
      specialization: form.specialization,
      qualification: form.qualification, consultationFee: Number(form.consultationFee)||0, departmentId: form.departmentId ? Number(form.departmentId) : null,
    };
    try {
      if (editing) {
        await adminApi.updateDoctor(editing.doctorId, payload);
        toast.success("Doctor updated.");
      } else {
        await adminApi.registerDoctor(payload);
        toast.success("Doctor registered.");
      }
      setModalOpen(false);
      load();
    } catch (err) {
      setFormError(err.friendlyMessage || "Could not save doctor.");
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await adminApi.deleteDoctor(confirmTarget.doctorId);
      toast.success("Doctor removed.");
      setConfirmTarget(null);
      load();
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not delete doctor.");
    } finally {
      setDeleting(false);
    }
  };

  const specOptions = useMemo(
    () => SPECIALIZATIONS.map((s) => ({ value: s, label: s.replaceAll("_", " ") })),
    []
  );

  if (loading) return <FullPageLoader label="Loading doctors…" />;

  return (
    <div>
      <PageHeader
        eyebrow="Admin"
        title="Doctors"
        subtitle="Register, edit and manage doctor accounts and specializations."
        action={
          <button className="btn-primary" onClick={openCreate}>
            + Register doctor
          </button>
        }
      />

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
        <StatCard label="Total doctors" value={doctors.length} icon="🩺" />
        {specOptions.slice(0, 3).map((s) => (
          <StatCard
            key={s.value}
            label={s.label}
            value={doctors.filter((d) => d.specialization === s.value).length}
            icon="•"
            accent="ink"
          />
        ))}
      </div>

      <DataTable
        data={doctors}
        rowKey={(r) => r.doctorId}
        searchKeys={["name", "email", "phone"]}
        searchPlaceholder="Search doctors by name, email, phone…"
        filters={[{ key: "specialization", label: "Specialization", options: specOptions }]}
        columns={[
          { key: "doctorId", label: "ID", width: "70px" },
          { key: "name", label: "Name" },
          { key: "email", label: "Email" },
          { key: "phone", label: "Phone" },
          { key: "qualification", label: "Qualification" },
          { key: "consultationFee", label: "Consultation", render: (r) => `₹${Number(r.consultationFee || 0).toLocaleString()}` },
          { key: "departmentName", label: "Department" },
          { key: "age", label: "Age", width: "80px" },
          {
            key: "specialization",
            label: "Specialization",
            render: (r) => <span className="badge bg-ink-100 text-ink-700">{r.specialization?.replaceAll("_", " ")}</span>,
          },
          { key: "salary", label: "Salary", render: (r) => `₹${Number(r.salary || 0).toLocaleString()}` },
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
        title={editing ? "Edit doctor" : "Register doctor"}
        subtitle={editing ? `Updating ${editing.name}` : "Creates a login account with the DOCTOR role."}
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
                placeholder={editing ? "••••••••" : ""}
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
              <label className="label">Age</label>
              <input type="number" min="0" className="input" value={form.age} onChange={(e) => setForm({ ...form, age: e.target.value })} />
            </div>
            <div>
              <label className="label">Salary</label>
              <input type="number" min="0" className="input" value={form.salary} onChange={(e) => setForm({ ...form, salary: e.target.value })} />
            </div>
            <div>
              <label className="label">Specialization</label>
              <select className="input" value={form.specialization} onChange={(e) => setForm({ ...form, specialization: e.target.value })}>
                {SPECIALIZATIONS.map((s) => (
                  <option key={s} value={s}>
                    {s.replaceAll("_", " ")}
                  </option>
                ))}
              </select>
            </div>
          </FormRow>

          <ErrorText>{formError}</ErrorText>

          <FormRow>
            <div><label className="label">Qualification</label><input className="input" value={form.qualification} onChange={(e) => setForm({ ...form, qualification: e.target.value })} placeholder="MBBS, MD…" /></div>
            <div><label className="label">Consultation fee</label><input className="input" type="number" min="0" value={form.consultationFee} onChange={(e) => setForm({ ...form, consultationFee: e.target.value })} /></div>
          </FormRow>
          <div><label className="label">Department ID (optional)</label><input className="input" type="number" value={form.departmentId} onChange={(e) => setForm({ ...form, departmentId: e.target.value })} placeholder="Create department first, then assign its ID" /></div>

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn-outline" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Saving…" : editing ? "Save changes" : "Register doctor"}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!confirmTarget}
        onClose={() => setConfirmTarget(null)}
        onConfirm={onDelete}
        loading={deleting}
        title="Delete doctor"
        message={`Remove ${confirmTarget?.name} from the system? This cannot be undone.`}
        confirmLabel="Delete"
      />
    </div>
  );
}
