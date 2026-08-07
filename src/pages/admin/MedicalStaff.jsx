import { useEffect, useState } from "react";
import * as adminApi from "../../api/admin";
import DataTable from "../../components/DataTable";
import Modal from "../../components/Modal";
import ConfirmDialog from "../../components/ConfirmDialog";
import { PageHeader, FormRow, ErrorText, FullPageLoader, StatCard } from "../../components/Atoms";
import { useToast } from "../../context/ToastContext";

const DEPARTMENTS = ["Pharmacy", "Billing", "Store"];
const GENDERS = ["Male", "Female", "Other"];

const emptyForm = { name: "", email: "", password: "", phone: "", gender: GENDERS[0], department: DEPARTMENTS[0] };

export default function MedicalStaff() {
  const toast = useToast();
  const [staff, setStaff] = useState([]);
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
      const data = await adminApi.getAllMedical();
      setStaff(data || []);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not load medical staff.");
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
    setForm(emptyForm);
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
      gender: row.gender || GENDERS[0],
      department: row.department || DEPARTMENTS[0],
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
    try {
      if (editing) {
        await adminApi.updateMedical(editing.id, form);
        toast.success("Staff member updated.");
      } else {
        await adminApi.registerMedical(form);
        toast.success("Medical staff registered.");
      }
      setModalOpen(false);
      load();
    } catch (err) {
      setFormError(err.friendlyMessage || "Could not save staff member.");
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async () => {
    if (!confirmTarget) return;
    setDeleting(true);
    try {
      await adminApi.deleteMedical(confirmTarget.id);
      toast.success("Staff member removed.");
      setConfirmTarget(null);
      load();
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not delete staff member.");
    } finally {
      setDeleting(false);
    }
  };

  if (loading) return <FullPageLoader label="Loading medical staff…" />;

  return (
    <div>
      <PageHeader
        eyebrow="Admin"
        title="Medical Staff"
        subtitle="Pharmacy, billing and store staff who record medicine charges and print receipts."
        action={
          <button className="btn-primary" onClick={openCreate}>
            + Register staff
          </button>
        }
      />

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
        <StatCard label="Total staff" value={staff.length} icon="💊" />
        {DEPARTMENTS.map((d) => (
          <StatCard key={d} label={d} value={staff.filter((s) => s.department === d).length} icon="•" accent="amber" />
        ))}
      </div>

      <DataTable
        data={staff}
        rowKey={(r) => r.id}
        searchKeys={["name", "email", "phone"]}
        searchPlaceholder="Search staff by name, email, phone…"
        filters={[{ key: "department", label: "Department", options: DEPARTMENTS.map((d) => ({ value: d, label: d })) }]}
        columns={[
          { key: "id", label: "ID", width: "70px" },
          { key: "name", label: "Name" },
          { key: "email", label: "Email" },
          { key: "phone", label: "Phone" },
          { key: "gender", label: "Gender" },
          { key: "department", label: "Department", render: (r) => <span className="badge bg-amber-100 text-amber-800">{r.department}</span> },
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
        title={editing ? "Edit staff member" : "Register medical staff"}
        subtitle={editing ? `Updating ${editing.name}` : "Creates a login account with the MEDICAL role."}
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
          <FormRow>
            <div>
              <label className="label">Gender</label>
              <select className="input" value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
                {GENDERS.map((g) => (
                  <option key={g} value={g}>
                    {g}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Department</label>
              <select className="input" value={form.department} onChange={(e) => setForm({ ...form, department: e.target.value })}>
                {DEPARTMENTS.map((d) => (
                  <option key={d} value={d}>
                    {d}
                  </option>
                ))}
              </select>
            </div>
          </FormRow>

          <ErrorText>{formError}</ErrorText>

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn-outline" onClick={() => setModalOpen(false)}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Saving…" : editing ? "Save changes" : "Register staff"}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!confirmTarget}
        onClose={() => setConfirmTarget(null)}
        onConfirm={onDelete}
        loading={deleting}
        title="Delete staff member"
        message={`Remove ${confirmTarget?.name} from the system? This cannot be undone.`}
        confirmLabel="Delete"
      />
    </div>
  );
}
