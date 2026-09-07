## Meridian Care — Hospital Management Frontend
 ###dq Ttt
A React (Vite) frontend for the Spring Boot Hospital Management System backend,
with a role-based dashboard for **Admin**, **Doctor**, **Nurse** and **Medical**
(pharmacy/billing) staff.

## What's included

- **JWT login** against `POST /api/auth/login`, token stored and attached to
  every request automatically (`Authorization: Bearer <token>`).
- **Role-based routing** — each role only sees and can reach its own pages;
  visiting another role's URL redirects home.
- **Admin** — register/edit/delete Doctors and Medical staff.
- **Doctor** — admit/edit/discharge patients, create/edit/delete Nurses.
- **Nurse** — update treatment notes & status, add medicines, print receipts.
- **Medical** — bill medicines to a patient, edit rates, view the itemised
  bill with an auto-computed grand total, and print receipts.
- **Search, column sorting, per-column filters and pagination** on every
  data table (see "About pagination/sorting/filtering" below).
- Toast notifications, confirm dialogs, and readable error messages parsed
  from the backend's `GlobalExceptionHandler` responses.

## Requirements

- Node.js 18+
- The Spring Boot backend from `Hospital/` running locally (default
  `http://localhost:8080`) with a reachable MySQL database.

## Setup

```bash
cd hospital-frontend
npm install
cp .env.example .env      # edit VITE_API_BASE_URL if your backend isn't on :8080
npm run dev                # http://localhost:5173
```

Build for production:

```bash
npm run build      # outputs to dist/
npm run preview    # serve the production build locally
```

## Backend

Run the Spring Boot app in `Hospital/` (via your IDE, `./mvnw spring-boot:run`,
or `java -jar`) after setting these environment variables (used by
`application.properties`):

```
DB_USERNAME=...
DB_PASSWORD=...
ADMIN_NAME=...
ADMIN_EMAIL=...
ADMIN_PASSWORD=...
```

A MySQL database named `hospital` must exist (`spring.jpa.hibernate.ddl-auto=update`
will create the tables). On first boot the app seeds one ADMIN user from the
`ADMIN_*` variables above — log in with that account first to create Doctors,
who can then create Nurses, and Admin can also create Medical staff.

### Backend fixes made while wiring up the frontend

While building the frontend against the real API, two backend bugs were fixed
(both contained, no behavior removed):

1. **`DELETE /api/admin/deletedoctors/{id}` was a no-op.** `deleteDoctor()` in
   `AdminServceImp` was an empty `// TODO` stub, so doctors were never actually
   removed (the controller's response message even referenced the "deleted"
   doctor right after, which would have thrown once the delete was fixed). It
   now deletes the `Doctor` row (and its linked login via the existing
   `cascade = ALL` on `Doctor.user`).
2. **Login didn't tell the frontend a Doctor/Nurse/Medical/Patient's own record
   ID.** `POST /api/auth/login` now also returns `profileId` — the caller's own
   `Doctor.id` / `Nurse.id` / `Medical.id` / `Patient.id` (`null` for Admin).
   The frontend uses this to pre-fill the "attending doctor" field when a
   Doctor admits a patient or creates a Nurse, instead of asking them to type
   their own ID from memory.

## About pagination / sorting / filtering

The existing endpoints (`/api/admin/getalldoctors`, `/api/doctor/allpatients`,
etc.) all return a plain, unpaginated `List<...>` — there's no `page`/`size`/
`sort` query param support on the backend. Rather than change the API
contracts, every list in this frontend goes through a shared `<DataTable>`
component (`src/components/DataTable.jsx`) that does this **client-side**:
search box, sortable column headers, per-column dropdown filters (e.g. filter
doctors by specialization, patients by status), and pagination with a
configurable page size. If you later add real server-side paging to the
Spring endpoints, only the `api/*.js` calls and this component's data source
need to change — the UI/UX stays the same.

## Project structure

```
src/
  api/            axios client + one module per backend controller
  components/     DataTable, Modal, ConfirmDialog, Layout, ProtectedRoute, Atoms
  context/        AuthContext (JWT/session), ToastContext (notifications)
  pages/
    Login.jsx
    admin/        Doctors.jsx, MedicalStaff.jsx
    doctor/       Patients.jsx, Nurses.jsx
    nurse/        Patients.jsx
    medical/      Patients.jsx
  utils/roleHome.js
```

## Login credentials

There's no public sign-up — accounts are created by an Admin (Doctors,
Medical staff) or a Doctor (Nurses; and Doctors admit Patients). Log in first
with the seeded Admin account from your `.env`/environment variables.
