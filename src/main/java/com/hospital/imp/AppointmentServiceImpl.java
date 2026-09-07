package com.hospital.imp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.Dto.AppointmentRequest;
import com.hospital.Dto.AppointmentResponse;
import com.hospital.enums.AppointmentStatus;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.DoctorSchedule;
import com.hospital.model.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorLeaveRepository;
import com.hospital.repository.DoctorScheduleRepository;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repo;
    private final PatientRepository patients;
    private final DocterRepository doctors;
    private final DoctorScheduleRepository schedules;
    private final DoctorLeaveRepository leaves;

    public AppointmentServiceImpl(AppointmentRepository repo,
                                  PatientRepository patients,
                                  DocterRepository doctors,
                                  DoctorScheduleRepository schedules,
                                  DoctorLeaveRepository leaves) {
        this.repo = repo;
        this.patients = patients;
        this.doctors = doctors;
        this.schedules = schedules;
        this.leaves = leaves;
    }

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentRequest r) {
        validate(r);
        Patient p = patients.findById(r.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor d = doctors.findById(r.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        checkSlot(d, r.getAppointmentDate(), r.getTimeSlot());

        Appointment a = Appointment.builder()
                .patient(p)
                .doctor(d)
                .appointmentDate(r.getAppointmentDate())
                .timeSlot(r.getTimeSlot())
                .reason(r.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        return map(repo.save(a));
    }

    @Override
    public List<AppointmentResponse> all() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Appointment::getAppointmentDate).thenComparing(Appointment::getTimeSlot))
                .map(this::map)
                .toList();
    }

    @Override
    public AppointmentResponse get(Long id) {
        return map(repo.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found")));
    }

    @Override
    @Transactional
    public AppointmentResponse update(Long id, AppointmentRequest r) {
        Appointment a = repo.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        if ((r.getAppointmentDate() != null && !r.getAppointmentDate().equals(a.getAppointmentDate())) ||
                (r.getTimeSlot() != null && !r.getTimeSlot().equals(a.getTimeSlot()))) {
            checkSlot(a.getDoctor(),
                    r.getAppointmentDate() == null ? a.getAppointmentDate() : r.getAppointmentDate(),
                    r.getTimeSlot() == null ? a.getTimeSlot() : r.getTimeSlot());
        }
        if (r.getAppointmentDate() != null) a.setAppointmentDate(r.getAppointmentDate());
        if (r.getTimeSlot() != null) a.setTimeSlot(r.getTimeSlot());
        if (r.getReason() != null) a.setReason(r.getReason());
        return map(repo.save(a));
    }

    @Override
    public AppointmentResponse status(Long id, AppointmentStatus s) {
        Appointment a = repo.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        a.setStatus(s);
        return map(repo.save(a));
    }

    @Override
    public void cancel(Long id) {
        status(id, AppointmentStatus.CANCELLED);
    }

    @Override
    public List<AppointmentResponse> byDoctor(Long id) {
        return repo.findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(id).stream().map(this::map).toList();
    }

    @Override
    public List<AppointmentResponse> byPatient(Long id) {
        return repo.findByPatientIdOrderByAppointmentDateDescTimeSlotDesc(id).stream().map(this::map).toList();
    }

    private void validate(AppointmentRequest r) {
        if (r.getPatientId() == null || r.getDoctorId() == null || r.getAppointmentDate() == null || r.getTimeSlot() == null) {
            throw new RuntimeException("Patient, doctor, date and time are required");
        }
        if (r.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Appointment date cannot be in the past");
        }
    }

    private void checkSlot(Doctor d, LocalDate date, LocalTime time) {
        if (leaves.existsByDoctorIdAndLeaveDate(d.getId(), date)) {
            throw new RuntimeException("Doctor is on leave on this date");
        }

        List<DoctorSchedule> docSchedules = schedules.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(d.getId())
                .stream().filter(DoctorSchedule::isActive).toList();

        if (!docSchedules.isEmpty()) {
            boolean valid = docSchedules.stream().anyMatch(s ->
                    s.getDayOfWeek() == date.getDayOfWeek()
                    && !time.isBefore(s.getStartTime())
                    && time.plusMinutes(s.getSlotMinutes()).compareTo(s.getEndTime()) <= 0);
            if (!valid) {
                throw new RuntimeException("Selected time is outside doctor's available schedule");
            }
        } else {
            // Default hospital operating hours fallback
            if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(20, 0))) {
                throw new RuntimeException("Appointment time must be between 08:00 and 20:00");
            }
        }

        if (repo.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(d.getId(), date, time, AppointmentStatus.CANCELLED)) {
            throw new RuntimeException("Doctor slot is already booked for this time");
        }
    }

    private AppointmentResponse map(Appointment a) {
        String patientName = (a.getPatient() != null && a.getPatient().getUser() != null)
                ? a.getPatient().getUser().getName() : "Patient #" + a.getPatient().getId();
        String doctorName = (a.getDoctor() != null && a.getDoctor().getUser() != null)
                ? a.getDoctor().getUser().getName() : "Doctor #" + a.getDoctor().getId();

        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .doctorId(a.getDoctor().getId())
                .patientName(patientName)
                .doctorName(doctorName)
                .appointmentDate(a.getAppointmentDate())
                .timeSlot(a.getTimeSlot())
                .status(a.getStatus())
                .reason(a.getReason())
                .build();
    }
}
