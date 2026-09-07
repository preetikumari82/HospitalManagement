package com.hospital.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.enums.AdmissionStatus;
import com.hospital.enums.AppointmentStatus;
import com.hospital.enums.BedStatus;
import com.hospital.enums.LabTestStatus;
import com.hospital.enums.PaymentStatus;
import com.hospital.enums.Role;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.BedRepository;
import com.hospital.repository.BillRepository;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.LabTestRepository;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.MedicineStockRepository;
import com.hospital.repository.NurseRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final BillRepository bills;
    private final BedRepository beds;
    private final AdmissionRepository admissions;
    private final DocterRepository doctors;
    private final NurseRepository nurses;
    private final UserRepository users;
    private final DepartmentRepository departments;
    private final LabTestRepository labTests;
    private final MedicineStockRepository medicineStock;
    private final MedicalRecordRepository medicalRecords;

    public DashboardController(PatientRepository patients,
                               AppointmentRepository appointments,
                               BillRepository bills,
                               BedRepository beds,
                               AdmissionRepository admissions,
                               DocterRepository doctors,
                               NurseRepository nurses,
                               UserRepository users,
                               DepartmentRepository departments,
                               LabTestRepository labTests,
                               MedicineStockRepository medicineStock,
                               MedicalRecordRepository medicalRecords) {
        this.patients = patients;
        this.appointments = appointments;
        this.bills = bills;
        this.beds = beds;
        this.admissions = admissions;
        this.doctors = doctors;
        this.nurses = nurses;
        this.users = users;
        this.departments = departments;
        this.labTests = labTests;
        this.medicineStock = medicineStock;
        this.medicalRecords = medicalRecords;
    }

    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> adminSummary() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        Double revenue = bills.sumPaidBetween(firstOfMonth.atStartOfDay(), today.plusDays(1).atStartOfDay());
        if (revenue == null) revenue = 0.0;

        long totalBeds = beds.count();
        long occupiedBeds = beds.findByStatus(BedStatus.OCCUPIED).size();
        long availableBeds = Math.max(0, totalBeds - occupiedBeds);
        double occupancyPercent = totalBeds == 0 ? 0.0 : (occupiedBeds * 100.0 / totalBeds);

        long lowStockCount = medicineStock.findAll().stream()
                .filter(m -> m.getAvailableQuantity() == null || m.getAvailableQuantity() <= 10)
                .count();

        long pendingLabs = labTests.findAll().stream()
                .filter(l -> l.getStatus() == LabTestStatus.REQUESTED || l.getStatus() == LabTestStatus.IN_PROGRESS)
                .count();

        Map<String, Object> map = new HashMap<>();
        map.put("totalPatients", patients.count());
        map.put("todayAppointments", appointments.countByAppointmentDate(today));
        map.put("totalRevenueMTD", revenue);
        map.put("totalBeds", totalBeds);
        map.put("occupiedBeds", occupiedBeds);
        map.put("availableBeds", availableBeds);
        map.put("bedOccupancyPercent", occupancyPercent);
        map.put("activeAdmissions", admissions.findByStatus(AdmissionStatus.ACTIVE).size());
        map.put("totalDoctors", doctors.count());
        map.put("totalNurses", nurses.count());
        map.put("totalStaff", users.count() - patients.count());
        map.put("totalDepartments", departments.count());
        map.put("pendingLabTests", pendingLabs);
        map.put("lowStockCount", lowStockCount);
        return map;
    }

    @GetMapping("/doctor/{doctorId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Map<String, Object> doctorSummary(@PathVariable Long doctorId) {
        return getDoctorMetrics(doctorId);
    }

    @GetMapping("/doctor/me/summary")
    @PreAuthorize("hasRole('DOCTOR')")
    public Map<String, Object> doctorMeSummary(Authentication auth) {
        User user = users.findByEmail(auth.getName()).orElse(null);
        if (user == null) return Map.of("todayAppointments", 0, "pendingAppointments", 0, "completedAppointments", 0);
        Doctor doc = doctors.findByUserId(user.getId()).orElse(null);
        if (doc == null) return Map.of("todayAppointments", 0, "pendingAppointments", 0, "completedAppointments", 0);
        return getDoctorMetrics(doc.getId());
    }

    private Map<String, Object> getDoctorMetrics(Long doctorId) {
        LocalDate today = LocalDate.now();
        var docAppointments = appointments.findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(doctorId);

        long todayCount = docAppointments.stream()
                .filter(a -> today.equals(a.getAppointmentDate()))
                .count();

        long pendingCount = docAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED)
                .count();

        long completedCount = docAppointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count();

        long totalAssignedPatients = docAppointments.stream()
                .map(a -> a.getPatient().getId())
                .distinct()
                .count();

        Map<String, Object> map = new HashMap<>();
        map.put("todayAppointments", todayCount);
        map.put("pendingAppointments", pendingCount);
        map.put("completedAppointments", completedCount);
        map.put("totalPatientsAssigned", totalAssignedPatients);
        return map;
    }

    @GetMapping("/patient/{patientId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT','RECEPTIONIST')")
    public Map<String, Object> patientSummary(@PathVariable Long patientId) {
        return getPatientMetrics(patientId);
    }

    @GetMapping("/patient/me/summary")
    @PreAuthorize("hasRole('PATIENT')")
    public Map<String, Object> patientMeSummary(Authentication auth) {
        User user = users.findByEmail(auth.getName()).orElse(null);
        if (user == null) return Map.of();
        Patient pat = patients.findByUserId(user.getId()).orElse(null);
        if (pat == null) return Map.of();
        return getPatientMetrics(pat.getId());
    }

    private Map<String, Object> getPatientMetrics(Long patientId) {
        LocalDate today = LocalDate.now();
        var patientAppointments = appointments.findByPatientIdOrderByAppointmentDateDescTimeSlotDesc(patientId);

        long upcomingAppointments = patientAppointments.stream()
                .filter(a -> !a.getAppointmentDate().isBefore(today) && a.getStatus() != AppointmentStatus.CANCELLED)
                .count();

        var patientBills = bills.findByPatientIdOrderByCreatedAtDesc(patientId);
        long pendingBills = patientBills.stream()
                .filter(b -> b.getStatus() != PaymentStatus.PAID)
                .count();

        double outstandingAmount = patientBills.stream()
                .filter(b -> b.getStatus() != PaymentStatus.PAID)
                .mapToDouble(b -> Math.max(0.0, b.getTotalAmount() - b.getPaidAmount()))
                .sum();

        long labReportsCount = labTests.findByPatientIdOrderByRequestedAtDesc(patientId).size();
        long medicalRecordsCount = medicalRecords.findByPatientIdOrderByCreatedAtDesc(patientId).size();

        Map<String, Object> map = new HashMap<>();
        map.put("upcomingAppointments", upcomingAppointments);
        map.put("pendingBills", pendingBills);
        map.put("outstandingAmount", outstandingAmount);
        map.put("labReportsCount", labReportsCount);
        map.put("medicalRecordsCount", medicalRecordsCount);
        return map;
    }

    @GetMapping("/nurse/summary")
    @PreAuthorize("hasAnyRole('ADMIN','NURSE')")
    public Map<String, Object> nurseSummary() {
        long totalBeds = beds.count();
        long occupiedBeds = beds.findByStatus(BedStatus.OCCUPIED).size();
        long activeAdmissions = admissions.findByStatus(AdmissionStatus.ACTIVE).size();

        Map<String, Object> map = new HashMap<>();
        map.put("totalBeds", totalBeds);
        map.put("occupiedBeds", occupiedBeds);
        map.put("availableBeds", Math.max(0, totalBeds - occupiedBeds));
        map.put("activeAdmissions", activeAdmissions);
        return map;
    }

    @GetMapping("/receptionist/summary")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Map<String, Object> receptionistSummary() {
        LocalDate today = LocalDate.now();
        long todayAppts = appointments.countByAppointmentDate(today);
        long totalBeds = beds.count();
        long occupiedBeds = beds.findByStatus(BedStatus.OCCUPIED).size();

        Map<String, Object> map = new HashMap<>();
        map.put("todayAppointments", todayAppts);
        map.put("totalPatients", patients.count());
        map.put("totalBeds", totalBeds);
        map.put("availableBeds", Math.max(0, totalBeds - occupiedBeds));
        map.put("activeAdmissions", admissions.findByStatus(AdmissionStatus.ACTIVE).size());
        return map;
    }

    @GetMapping("/pharmacy/summary")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICAL','PHARMACIST')")
    public Map<String, Object> pharmacySummary() {
        var stock = medicineStock.findAll();
        long totalItems = stock.size();
        long lowStock = stock.stream().filter(m -> m.getAvailableQuantity() == null || m.getAvailableQuantity() <= 10).count();
        long inStock = totalItems - lowStock;

        Map<String, Object> map = new HashMap<>();
        map.put("totalMedicines", totalItems);
        map.put("inStockMedicines", inStock);
        map.put("lowStockMedicines", lowStock);
        return map;
    }

    @GetMapping("/lab/summary")
    @PreAuthorize("hasAnyRole('ADMIN','LAB_TECH')")
    public Map<String, Object> labSummary() {
        var allTests = labTests.findAll();
        long pending = allTests.stream().filter(t -> t.getStatus() == LabTestStatus.REQUESTED).count();
        long inProgress = allTests.stream().filter(t -> t.getStatus() == LabTestStatus.IN_PROGRESS).count();
        long completed = allTests.stream().filter(t -> t.getStatus() == LabTestStatus.COMPLETED).count();

        Map<String, Object> map = new HashMap<>();
        map.put("pendingTests", pending);
        map.put("inProgressTests", inProgress);
        map.put("completedTests", completed);
        map.put("totalTests", allTests.size());
        return map;
    }
}
