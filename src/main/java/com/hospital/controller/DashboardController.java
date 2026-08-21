package com.hospital.controller;

import com.hospital.enums.*;
import com.hospital.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final BillRepository bills;
    private final BedRepository beds;
    private final AdmissionRepository admissions;

    public DashboardController(PatientRepository patients, AppointmentRepository appointments, BillRepository bills,
                               BedRepository beds, AdmissionRepository admissions) {
        this.patients = patients; this.appointments = appointments; this.bills = bills;
        this.beds = beds; this.admissions = admissions;
    }

    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public Object adminSummary() {
        LocalDate today = LocalDate.now();
        LocalDate first = today.withDayOfMonth(1);
        double revenue = bills.sumPaidBetween(first.atStartOfDay(), today.plusDays(1).atStartOfDay());
        long totalBeds = beds.count(), occupied = beds.findByStatus(BedStatus.OCCUPIED).size();
        return java.util.Map.of(
            "totalPatients", patients.count(),
            "todayAppointments", appointments.countByAppointmentDate(today),
            "totalRevenueMTD", revenue,
            "totalBeds", totalBeds,
            "occupiedBeds", occupied,
            "bedOccupancyPercent", totalBeds == 0 ? 0.0 : (occupied * 100.0 / totalBeds),
            "activeAdmissions", admissions.findByStatus(AdmissionStatus.ACTIVE).size()
        );
    }

    @GetMapping("/doctor/{doctorId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Object doctorSummary(@PathVariable Long doctorId) {
        LocalDate today = LocalDate.now();
        return java.util.Map.of(
            "todayAppointments", appointments.findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(doctorId).stream()
                .filter(a -> today.equals(a.getAppointmentDate())).count(),
            "pendingAppointments", appointments.findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(doctorId).stream()
                .filter(a -> a.getStatus() == AppointmentStatus.PENDING || a.getStatus() == AppointmentStatus.CONFIRMED).count()
        );
    }
}
