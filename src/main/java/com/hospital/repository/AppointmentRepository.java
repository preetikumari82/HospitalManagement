package com.hospital.repository;
import java.time.*; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import com.hospital.enums.AppointmentStatus; import com.hospital.model.Appointment;
public interface AppointmentRepository extends JpaRepository<Appointment,Long>{ 
 long countByAppointmentDate(LocalDate date); 
 long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status); boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(Long doctorId,LocalDate date,LocalTime time,AppointmentStatus status); List<Appointment> findByDoctorIdOrderByAppointmentDateAscTimeSlotAsc(Long doctorId); List<Appointment> findByPatientIdOrderByAppointmentDateDescTimeSlotDesc(Long patientId); List<Appointment> findByAppointmentDateAndStatus(LocalDate date,AppointmentStatus status); List<Appointment> findByAppointmentDateAndReminderSentFalse(LocalDate date); }
