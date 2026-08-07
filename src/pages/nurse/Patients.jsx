import { useEffect, useState } from "react";
import * as nurseApi from "../../api/nurse";
import DataTable from "../../components/DataTable";
import Modal from "../../components/Modal";
import { PageHeader, FormRow, ErrorText, FullPageLoader, StatCard, StatusBadge } from "../../components/Atoms";
import { useToast } from "../../context/ToastContext";

const STATUSES = ["ADMITTED", "UNDER_TREATMENT", "DISCHARGED", "REFERRED"];

export default function NursePatients() {
  const toast = useToast();
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [treatTarget, setTreatTarget] = useState(null);
  const [treatForm, setTreatForm] = useState({ treatment: "", medicine: "", nurseRemarks: "" });
  const [treatError, setTreatError] = useState("");

  const [medTarget, setMedTarget] = useState(null);
  const [medForm, setMedForm] = useState({ doctor: "", date: new Date().toISOString().slice(0, 10), medicines: [{ medicineName: "", dosage: "", timing: "", quantity: 1, rate: "" }] });
  const [medError, setMedError] = useState("");

  const [receiptText, setReceiptText] = useState(null);
  const [receiptLoadingId, setReceiptLoadingId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const data = await nurseApi.getAllPatients();
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

  const openTreat = (row) => {
    setTreatTarget(row);
    setTreatForm({ treatment: row.treatment || "", medicine: row.medicine || "", nurseRemarks: row.nurseRemarks || "" });
    setTreatError("");
  };

  const onSaveTreatment = async (e) => {
    e.preventDefault();
    setSaving(true);
    setTreatError("");
    try {
      await nurseApi.updateTreatment(treatTarget.id, treatForm);
      toast.success("Treatment notes updated.");
      setTreatTarget(null);
      load();
    } catch (err) {
      setTreatError(err.friendlyMessage || "Could not update treatment.");
    } finally {
      setSaving(false);
    }
  };

  const onStatusChange = async (row, status) => {
    try {
      await nurseApi.updateStatus(row.id, { status });
      toast.success(`Status set to ${status.replaceAll("_", " ")}.`);
      load();
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not update status.");
    }
  };

  const openMed = (row) => {
    setMedTarget(row);
    setMedForm({ doctor: "", date: new Date().toISOString().slice(0, 10), medicines: [{ medicineName: "", dosage: "", timing: "", quantity: 1, rate: "" }] });
    setMedError("");
  };

  const updateMedRow = (idx, field, value) => {
    setMedForm((f) => {
      const medicines = [...f.medicines];
      medicines[idx] = { ...medicines[idx], [field]: value };
      return { ...f, medicines };
    });
  };

  const addMedRow = () =>
    setMedForm((f) => ({ ...f, medicines: [...f.medicines, { medicineName: "", dosage: "", timing: "", quantity: 1, rate: "" }] }));

  const removeMedRow = (idx) =>
    setMedForm((f) => ({ ...f, medicines: f.medicines.filter((_, i) => i !== idx) }));

  const onSaveMedicine = async (e) => {
    e.preventDefault();
    setMedError("");
    if (medForm.medicines.some((m) => !m.medicineName)) {
      setMedError("Every row needs a medicine name.");
      return;
    }
    setSaving(true);
    try {
      const payload = {
        ...medForm,
        medicines: medForm.medicines.map((m) => ({ ...m, quantity: Number(m.quantity) || 1, rate: Number(m.rate) || 0 })),
      };
      await nurseApi.addMedicine(medTarget.id, payload);
      toast.success("Medicine added to patient record.");
      setMedTarget(null);
      load();
    } catch (err) {
      setMedError(err.friendlyMessage || "Could not add medicine.");
    } finally {
      setSaving(false);
    }
  };

  const onPrintReceipt = async (row) => {
    setReceiptLoadingId(row.id);
    try {
      const text = await nurseApi.createReceipt(row.id);
      setReceiptText({ patient: row.name, text: typeof text === "string" ? text : JSON.stringify(text, null, 2) });
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not generate receipt.");
    } finally {
      setReceiptLoadingId(null);
    }
  };

  if (loading) return <FullPageLoader label="Loading patients…" />;

  return (
    <div>
      <PageHeader eyebrow="Nurse" title="Patients" subtitle="Chart treatment notes, update patient status, log medicines and print receipts." />

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
        searchPlaceholder="Search patients by name, disease…"
        filters={[{ key: "status", label: "Status", options: STATUSES.map((s) => ({ value: s, label: s.replaceAll("_", " ") })) }]}
        columns={[
          { key: "id", label: "ID", width: "60px" },
          { key: "name", label: "Name" },
          { key: "disease", label: "Disease" },
          {
            key: "status",
            label: "Status",
            render: (r) => (
              <select
                value={r.status || ""}
                onChange={(e) => onStatusChange(r, e.target.value)}
                className="text-xs rounded-md border border-line bg-white px-2 py-1"
                onClick={(e) => e.stopPropagation()}
              >
                {STATUSES.map((s) => (
                  <option key={s} value={s}>
                    {s.replaceAll("_", " ")}
                  </option>
                ))}
              </select>
            ),
          },
          { key: "treatment", label: "Treatment", render: (r) => <span className="text-ink-500">{r.treatment || "—"}</span> },
          {
            key: "actions",
            label: "Actions",
            sortable: false,
            render: (r) => (
              <div className="flex gap-2 flex-wrap">
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => openTreat(r)}>
                  Treatment
                </button>
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => openMed(r)}>
                  + Medicine
                </button>
                <button className="btn-secondary px-2.5 py-1 text-xs" onClick={() => onPrintReceipt(r)} disabled={receiptLoadingId === r.id}>
                  {receiptLoadingId === r.id ? "…" : "Receipt"}
                </button>
              </div>
            ),
          },
        ]}
      />

      {/* Treatment modal */}
      <Modal open={!!treatTarget} onClose={() => setTreatTarget(null)} title="Treatment & remarks" subtitle={treatTarget?.name}>
        <form onSubmit={onSaveTreatment} className="space-y-4">
          <div>
            <label className="label">Treatment</label>
            <textarea className="input min-h-20" value={treatForm.treatment} onChange={(e) => setTreatForm({ ...treatForm, treatment: e.target.value })} />
          </div>
          <div>
            <label className="label">Medicine notes</label>
            <textarea className="input min-h-16" value={treatForm.medicine} onChange={(e) => setTreatForm({ ...treatForm, medicine: e.target.value })} />
          </div>
          <div>
            <label className="label">Nurse remarks</label>
            <textarea className="input min-h-16" value={treatForm.nurseRemarks} onChange={(e) => setTreatForm({ ...treatForm, nurseRemarks: e.target.value })} />
          </div>
          <ErrorText>{treatError}</ErrorText>
          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn-outline" onClick={() => setTreatTarget(null)}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Saving…" : "Save"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Add medicine modal */}
      <Modal open={!!medTarget} onClose={() => setMedTarget(null)} title="Add medicine" subtitle={medTarget?.name} width="max-w-2xl">
        <form onSubmit={onSaveMedicine} className="space-y-4">
          <FormRow>
            <div>
              <label className="label">Prescribing doctor</label>
              <input className="input" value={medForm.doctor} onChange={(e) => setMedForm({ ...medForm, doctor: e.target.value })} placeholder="Dr. name" />
            </div>
            <div>
              <label className="label">Date</label>
              <input type="date" className="input" value={medForm.date} onChange={(e) => setMedForm({ ...medForm, date: e.target.value })} />
            </div>
          </FormRow>

          <div className="space-y-3">
            {medForm.medicines.map((m, idx) => (
              <div key={idx} className="grid grid-cols-12 gap-2 items-end border border-line rounded-lg p-3 bg-ink-50/40">
                <div className="col-span-12 sm:col-span-3">
                  <label className="label">Medicine</label>
                  <input className="input" value={m.medicineName} onChange={(e) => updateMedRow(idx, "medicineName", e.target.value)} required />
                </div>
                <div className="col-span-6 sm:col-span-2">
                  <label className="label">Dosage</label>
                  <input className="input" value={m.dosage} onChange={(e) => updateMedRow(idx, "dosage", e.target.value)} />
                </div>
                <div className="col-span-6 sm:col-span-2">
                  <label className="label">Timing</label>
                  <input className="input" value={m.timing} onChange={(e) => updateMedRow(idx, "timing", e.target.value)} placeholder="After meal" />
                </div>
                <div className="col-span-4 sm:col-span-2">
                  <label className="label">Qty</label>
                  <input type="number" min="1" className="input" value={m.quantity} onChange={(e) => updateMedRow(idx, "quantity", e.target.value)} />
                </div>
                <div className="col-span-4 sm:col-span-2">
                  <label className="label">Rate (₹)</label>
                  <input type="number" min="0" className="input" value={m.rate} onChange={(e) => updateMedRow(idx, "rate", e.target.value)} />
                </div>
                <div className="col-span-4 sm:col-span-1">
                  <button type="button" className="btn-ghost text-clay-600 px-2" onClick={() => removeMedRow(idx)} disabled={medForm.medicines.length === 1}>
                    ✕
                  </button>
                </div>
              </div>
            ))}
            <button type="button" className="btn-outline text-xs" onClick={addMedRow}>
              + Add another medicine
            </button>
          </div>

          <ErrorText>{medError}</ErrorText>

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" className="btn-outline" onClick={() => setMedTarget(null)}>
              Cancel
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Saving…" : "Add medicine"}
            </button>
          </div>
        </form>
      </Modal>

      {/* Receipt viewer */}
      <Modal open={!!receiptText} onClose={() => setReceiptText(null)} title="Receipt" subtitle={receiptText?.patient} width="max-w-md">
        <pre className="whitespace-pre-wrap font-mono text-xs bg-ink-950 text-clover-100 rounded-lg p-4 leading-relaxed">{receiptText?.text}</pre>
        <div className="flex justify-end gap-2 pt-4">
          <button className="btn-outline" onClick={() => window.print()}>
            Print
          </button>
          <button className="btn-primary" onClick={() => setReceiptText(null)}>
            Close
          </button>
        </div>
      </Modal>
    </div>
  );
}
