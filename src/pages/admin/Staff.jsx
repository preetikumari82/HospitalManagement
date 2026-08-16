import { useEffect, useState } from "react";
import {
  getStaff,
  createStaff,
  updateStaff,
  deactivateStaff,
  activateStaff,
} from "../../api/admin";
import { useToast } from "../../context/ToastContext";

const blank = {
  name: "",
  email: "",
  password: "",
  role: "RECEPTIONIST",
};

export default function Staff() {
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState(blank);
  const [edit, setEdit] = useState(null);

  const toast = useToast();

  // =========================
  // LOAD STAFF
  // =========================
  const load = async () => {
    try {
      const data = await getStaff();

      console.log("STAFF DATA:", data);

      setRows(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error("STAFF ERROR:", e);
      toast.error(e?.friendlyMessage || "Could not load staff");
    }
  };

  // IMPORTANT
  useEffect(() => {
    load();
  }, []);

  // =========================
  // CREATE / UPDATE
  // =========================
  const save = async (e) => {
    e.preventDefault();

    try {
      if (edit) {
        await updateStaff(edit, form);
        toast.success("Staff updated");
      } else {
        await createStaff(form);
        toast.success("Staff created");
      }

      setForm(blank);
      setEdit(null);

      // Reload list
      await load();

    } catch (e) {
      console.error("SAVE STAFF ERROR:", e);
      toast.error(e?.friendlyMessage || "Save failed");
    }
  };

  // =========================
  // EDIT
  // =========================
  const start = (r) => {
    setEdit(r.userId);

    setForm({
      name: r.name || "",
      email: r.email || "",
      password: "",
      role: r.role || "RECEPTIONIST",
    });
  };

  // =========================
  // DEACTIVATE
  // =========================
  const handleDeactivate = async (id) => {
    try {
      await deactivateStaff(id);
      toast.success("Staff deactivated");
      await load();
    } catch (e) {
      toast.error(e?.friendlyMessage || "Could not deactivate staff");
    }
  };

  // =========================
  // ACTIVATE
  // =========================
  const handleActivate = async (id) => {
    try {
      await activateStaff(id);
      toast.success("Staff activated");
      await load();
    } catch (e) {
      toast.error(e?.friendlyMessage || "Could not activate staff");
    }
  };

  return (
    <div>

      {/* HEADER */}
      <div className="mb-5">
        <h1 className="font-display text-2xl font-semibold">
          Staff Accounts
        </h1>

        <p className="text-sm text-ink-500">
          Create, update, activate or deactivate staff login accounts.
        </p>
      </div>

      {/* FORM */}
      <form
        onSubmit={save}
        className="bg-white border border-line rounded-xl p-4 grid sm:grid-cols-5 gap-3 mb-5"
      >

        <input
          className="input"
          placeholder="Name"
          value={form.name}
          onChange={(e) =>
            setForm({ ...form, name: e.target.value })
          }
          required
        />

        <input
          className="input"
          placeholder="Email"
          type="email"
          value={form.email}
          onChange={(e) =>
            setForm({ ...form, email: e.target.value })
          }
          required
        />

        <input
          className="input"
          placeholder={
            edit ? "New password (optional)" : "Password"
          }
          type="password"
          value={form.password}
          onChange={(e) =>
            setForm({ ...form, password: e.target.value })
          }
          required={!edit}
        />

        <select
          className="input"
          value={form.role}
          onChange={(e) =>
            setForm({ ...form, role: e.target.value })
          }
        >
          <option value="RECEPTIONIST">RECEPTIONIST</option>
          <option value="PHARMACIST">PHARMACIST</option>
          <option value="LAB_TECH">LAB TECH</option>
        </select>

        <div className="flex gap-2">
          <button type="submit" className="btn-primary">
            {edit ? "Update" : "Create"}
          </button>

          {edit && (
            <button
              type="button"
              className="btn-outline"
              onClick={() => {
                setEdit(null);
                setForm(blank);
              }}
            >
              Cancel
            </button>
          )}
        </div>
      </form>

      {/* TABLE */}
      <div className="bg-white border border-line rounded-xl overflow-auto">

        <table className="w-full text-sm">

          <thead>
            <tr className="border-b border-line text-left">
              <th className="p-3">ID</th>
              <th className="p-3">Name</th>
              <th className="p-3">Email</th>
              <th className="p-3">Role</th>
              <th className="p-3">Status</th>
              <th className="p-3">Actions</th>
            </tr>
          </thead>

          <tbody>

            {rows.length === 0 ? (
              <tr>
                <td
                  colSpan="6"
                  className="p-6 text-center text-ink-400"
                >
                  No staff accounts found.
                </td>
              </tr>
            ) : (
              rows.map((r) => (
                <tr
                  key={r.userId}
                  className="border-b border-line"
                >
                  <td className="p-3">
                    #{r.userId}
                  </td>

                  <td className="p-3 font-semibold">
                    {r.name}
                  </td>

                  <td className="p-3">
                    {r.email}
                  </td>

                  <td className="p-3">
                    {r.role}
                  </td>

                  <td className="p-3">
                    {r.active ? "Active" : "Inactive"}
                  </td>

                  <td className="p-3">
                    <div className="flex gap-2">

                      <button
                        className="btn-outline text-xs"
                        onClick={() => start(r)}
                      >
                        Edit
                      </button>

                      {r.active ? (
                        <button
                          className="btn-outline text-xs"
                          onClick={() =>
                            handleDeactivate(r.userId)
                          }
                        >
                          Deactivate
                        </button>
                      ) : (
                        <button
                          className="btn-outline text-xs"
                          onClick={() =>
                            handleActivate(r.userId)
                          }
                        >
                          Activate
                        </button>
                      )}

                    </div>
                  </td>
                </tr>
              ))
            )}

          </tbody>
        </table>
      </div>
    </div>
  );
}