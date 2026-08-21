# Hospital Management System Backend

Spring Boot 3.5.x + Java 21 + MySQL + Spring Security/JWT.

## Run locally

1. Install Java 21 and MySQL 8.
2. Create database:
   `CREATE DATABASE hospital;`
3. Copy `.env.example` to `.env` and update DB credentials.
4. Start:
   - Windows: `mvnw.cmd spring-boot:run`
   - Linux/macOS: `bash mvnw spring-boot:run`
5. API base: `http://localhost:8080/api`
6. Swagger: `http://localhost:8080/swagger-ui/index.html`

The application uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created/updated automatically for development.

## Default development admin

If no environment variables are supplied:
- Email: `admin@hospital.com`
- Password: `admin123`

Change these before production.

## Main modules

Authentication/JWT, patient registration/history, doctor profiles, departments, doctor schedules/leaves, appointments with slot-conflict protection, EHR/medical records/prescriptions, laboratory, billing + PDF invoice, pharmacy/medicine stock, IPD beds/admissions/discharge, role-based security, password reset OTP, appointment reminders, and admin/doctor dashboard summaries.

## SRS alignment

The backend implements the core modules described in SRS v1.0. The SRS specifies React + Spring Boot + MySQL/PostgreSQL, JWT/RBAC, patient management, doctor scheduling, appointments, EHR, billing, pharmacy, laboratory, IPD, analytics, and audit/security requirements.

### Important production items

Configure HTTPS, a production JWT secret, real SMTP credentials, restricted CORS, database backups, and an audit-log sink before deployment. Insurance integration, telemedicine, and AI diagnosis are explicitly out of scope for SRS v1.0.

## API note

Existing frontend compatibility is preserved under `/api/...`. The SRS lists `/api/v1` as its preferred versioned base; versioning can be introduced later with controller aliases without changing the current frontend contract.
