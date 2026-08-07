import { useEffect, useState } from "react";
import * as medicalApi from "../../api/medical";
import DataTable from "../../components/DataTable";
import Modal from "../../components/Modal";
import { PageHeader, FormRow, ErrorText, FullPageLoader, StatCard, StatusBadge } from "../../components/Atoms";
import { useToast } from "../../context/ToastContext";

const STATUSES = ["ADMITTED", "UNDER_TREATMENT", "DISCHARGED", "REFERRED"];

export default function MedicalPatients() {
  const toast = useToast();
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [medTarget, setMedTarget] = useState(null);
  const [medForm, setMedForm] = useState({ doctor: "", date: new Date().toISOString().slice(0, 10), medicines: [{ medicineName: "", dosage: "", timing: "", quantity: 1, rate: "" }] });
  const [medError, setMedError] = useState("");

  const [billTarget, setBillTarget] = useState(null);
  const [bill, setBill] = useState(null);
  const [billLoading, setBillLoading] = useState(false);

  const [rateEdit, setRateEdit] = useState(null); // { medicineId, rate, quantity }
  const [rateError, setRateError] = useState("");

  const [receiptText, setReceiptText] = useState(null);
  const [receiptLoadingId, setReceiptLoadingId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const data = await medicalApi.getAllPatients();
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

  // ---- Add medicine ----
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
  const addMedRow = () => setMedForm((f) => ({ ...f, medicines: [...f.medicines, { medicineName: "", dosage: "", timing: "", quantity: 1, rate: "" }] }));
  const removeMedRow = (idx) => setMedForm((f) => ({ ...f, medicines: f.medicines.filter((_, i) => i !== idx) }));

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
      await medicalApi.addMedicine(medTarget.id, payload);
      toast.success("Medicine billed to patient.");
      setMedTarget(null);
      load();
    } catch (err) {
      setMedError(err.friendlyMessage || "Could not add medicine.");
    } finally {
      setSaving(false);
    }
  };

  // ---- View bill ----
  const openBill = async (row) => {
    setBillTarget(row);
    setBillLoading(true);
    setBill(null);
    try {
      const data = await medicalApi.getMedicineBill(row.id);
      setBill(data);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not load bill.");
      setBillTarget(null);
    } finally {
      setBillLoading(false);
    }
  };

  const onSaveRate = async (e) => {
    e.preventDefault();
    setRateError("");
    setSaving(true);
    try {
      await medicalApi.updateMedicineRate(rateEdit.medicineId, { rate: Number(rateEdit.rate) || 0, quantity: Number(rateEdit.quantity) || 1 });
      toast.success("Rate updated.");
      setRateEdit(null);
      if (billTarget) openBill(billTarget);
    } catch (err) {
      setRateError(err.friendlyMessage || "Could not update rate.");
    } finally {
      setSaving(false);
    }
  };

  const onPrintReceipt = async (row) => {
    setReceiptLoadingId(row.id);
    try {
      const text = await medicalApi.generateReceipt(row.id);
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
      <PageHeader eyebrow="Medical · Pharmacy & Billing" title="Patients" subtitle="Bill medicines to patients, adjust rates, review totals and print receipts." />

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
        <StatCard label="Total patients" value={patients.length} icon="🧾" />
        {STATUSES.slice(0, 3).map((s) => (
          <StatCard key={s} label={s.replaceAll("_", " ")} value={patients.filter((p) => p.status === s).length} icon="•" accent="amber" />
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
          { key: "status", label: "Status", render: (r) => <StatusBadge status={r.status} /> },
          { key: "fees", label: "Fees", render: (r) => `₹${Number(r.fees || 0).toLocaleString()}` },
          {
            key: "actions",
            label: "Actions",
            sortable: false,
            render: (r) => (
              <div className="flex gap-2 flex-wrap">
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => openMed(r)}>
                  + Medicine
                </button>
                <button className="btn-outline px-2.5 py-1 text-xs" onClick={() => openBill(r)}>
                  View bill
                </button>
                <button className="btn-secondary px-2.5 py-1 text-xs" onClick={() => onPrintReceipt(r)} disabled={receiptLoadingId === r.id}>
                  {receiptLoadingId === r.id ? "…" : "Receipt"}
                </button>
              </div>
            ),
          },
        ]}
      />

      {/* Add medicine modal */}
      <Modal open={!!medTarget} onClose={() => setMedTarget(null)} title="Bill medicine" subtitle={medTarget?.name} width="max-w-2xl">
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
                  <input className="input" value={m.timing} onChange={(e) => updateMedRow(idx, "timing", e.target.value)} />
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
              {saving ? "Saving…" : "Bill medicine"}
            </button>
          </div>
        </form>
      </Modal>

      {/* View bill modal */}
      <Modal open={!!billTarget} onClose={() => setBillTarget(null)} title="Medicine bill" subtitle={billTarget?.name} width="max-w-2xl">
        {billLoading && <p className="text-sm text-ink-400">Loading bill…</p>}
        {!billLoading && bill && (
          <div>
            {(!bill.medicines || bill.medicines.length === 0) && <p className="text-sm text-ink-400">No medicines billed yet.</p>}
            {bill.medicines && bill.medicines.length > 0 && (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-line text-left text-[11px] uppercase text-ink-400">
                      <th className="py-2 pr-2">Medicine</th>
                      <th className="py-2 pr-2">Dosage</th>
                      <th className="py-2 pr-2">Qty</th>
                      <th className="py-2 pr-2">Rate</th>
                      <th className="py-2 pr-2">Total</th>
                      <th className="py-2 pr-2"></th>
                    </tr>
                  </thead>
                  <tbody>
                    {bill.medicines.map((m) => (
                      <tr key={m.id} className="border-b border-line/60">
                        <td className="py-2 pr-2 font-medium text-ink-800">{m.medicineName}</td>
                        <td className="py-2 pr-2 text-ink-500">{m.dosage || "—"}</td>
                        <td className="py-2 pr-2">{m.quantity}</td>
                        <td className="py-2 pr-2">₹{Number(m.rate || 0).toLocaleString()}</td>
                        <td className="py-2 pr-2 font-semibold">₹{Number(m.total || 0).toLocaleString()}</td>
                        <td className="py-2 pr-2">
                          <button
                            className="btn-outline px-2 py-0.5 text-[11px]"
                            onClick={() => {
                              setRateEdit({ medicineId: m.id, rate: m.rate ?? "", quantity: m.quantity ?? 1 });
                              setRateError("");
                            }}
                          >
                            Edit rate
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                <div className="flex justify-end mt-4">
                  <div className="text-right">
                    <p className="text-xs text-ink-400 uppercase tracking-wide">Grand total</p>
                    <p className="font-display text-2xl font-semibold text-ink-900">₹{Number(bill.grandTotal || 0).toLocaleString()}</p>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}
      </Modal>

      {/* Edit rate modal */}
      <Modal open={!!rateEdit} onClose={() => setRateEdit(null)} title="Update medicine rate" width="max-w-sm">
        {rateEdit && (
          <form onSubmit={onSaveRate} className="space-y-4">
            <FormRow>
              <div>
                <label className="label">Rate (₹ per unit)</label>
                <input type="number" min="0" className="input" value={rateEdit.rate} onChange={(e) => setRateEdit({ ...rateEdit, rate: e.target.value })} required />
              </div>
              <div>
                <label className="label">Quantity</label>
                <input type="number" min="1" className="input" value={rateEdit.quantity} onChange={(e) => setRateEdit({ ...rateEdit, quantity: e.target.value })} />
              </div>
            </FormRow>
            <ErrorText>{rateError}</ErrorText>
            <div className="flex justify-end gap-2 pt-2">
              <button type="button" className="btn-outline" onClick={() => setRateEdit(null)}>
                Cancel
              </button>
              <button type="submit" className="btn-primary" disabled={saving}>
                {saving ? "Saving…" : "Update rate"}
              </button>
            </div>
          </form>
        )}
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
