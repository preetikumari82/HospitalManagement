# Hospital Management — Backend (Spring Boot)

## Deploying on Render

Java apps need **Docker** on Render (there's no native Java runtime there), and
Render doesn't offer managed MySQL — so you'll point it at an external MySQL
database. Full steps below.

### Files involved (already added to this project)

| File | Purpose |
|---|---|
| `Dockerfile` (project root, next to `pom.xml`) | Builds the jar with Maven, then runs it on a small JRE image. Render auto-detects this. |
| `.dockerignore` (project root) | Keeps `.env`, `target/`, IDE files out of the image. |
| `render.yaml` (project root, optional) | "Infra as code" — lets Render create the service from this repo automatically instead of clicking through the dashboard. |
| `src/main/resources/application.properties` | Already updated to read `DB_URL`, `PORT`, and `CORS_ALLOWED_ORIGINS` from environment variables instead of being hardcoded to `localhost`. |

You don't need to create any new files yourself — just push this project
(including the three new files above) to a GitHub repo.

### 1. Get a MySQL database reachable from the internet

Render itself doesn't host MySQL. Easiest free/cheap options: **Railway**,
**Aiven for MySQL**, **Clever Cloud**, or **PlanetScale**. Create a database
on any of these and note down: host, port, database name, username, password.

### 2. Push this project to GitHub

```bash
git init
git add .
git commit -m "Hospital backend"
git branch -M main
git remote add origin https://github.com/<you>/hospital-backend.git
git push -u origin main
```

### 3. Create the Web Service on Render

1. [dashboard.render.com](https://dashboard.render.com) → **New** → **Web Service**.
2. Connect the GitHub repo you just pushed.
3. Render should detect the `Dockerfile` and set **Runtime = Docker**
   automatically (pick it manually if not). Leave Build/Start commands empty
   — Docker handles both.
4. Root directory: wherever `Dockerfile`/`pom.xml` sit (repo root if you
   pushed only this project).
5. Under **Environment**, add these variables:

   | Key | Example value |
   |---|---|
   | `DB_URL` | `jdbc:mysql://<host>:<port>/hospital?useSSL=true&serverTimezone=UTC` |
   | `DB_USERNAME` | your MySQL username |
   | `DB_PASSWORD` | your MySQL password |
   | `ADMIN_NAME` | `Hospital Admin` |
   | `ADMIN_EMAIL` | `admin@hospital.com` |
   | `ADMIN_PASSWORD` | a strong password — this becomes the seeded ADMIN login |
   | `CORS_ALLOWED_ORIGINS` | your deployed frontend URL, e.g. `https://hospital-frontend.onrender.com` (comma-separate if more than one) |

   Do **not** set `PORT` — Render injects it automatically and
   `application.properties` already reads it.
6. Click **Create Web Service**. First build takes a few minutes (Maven
   downloads dependencies, compiles, then a small JRE image is built and
   started).
7. Once live, your API base URL is `https://<your-service-name>.onrender.com`.
   Test with: `https://<your-service-name>.onrender.com/api/auth/login`.

### 4. Point the frontend at it

In the frontend project, set `VITE_API_BASE_URL` to that Render URL (as a
Render **Static Site** env var if you deploy the frontend on Render too, or
in `.env` for local testing against the deployed backend).

### Notes

- Free-tier Render web services spin down after inactivity and take ~30–60s
  to wake on the next request — the first login after idle time will be slow,
  that's expected.
- `spring.jpa.hibernate.ddl-auto=update` will create tables automatically on
  first boot against your MySQL database — no manual schema needed.
- The seeded ADMIN account (from `ADMIN_EMAIL`/`ADMIN_PASSWORD`) is your first
  login; use it to register Doctors and Medical staff.
