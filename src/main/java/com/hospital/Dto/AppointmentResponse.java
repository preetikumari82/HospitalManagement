package com.hospital.Dto;
import java.time.*; import com.hospital.enums.AppointmentStatus; import lombok.*;
@Getter @Setter @Builder public class AppointmentResponse { private Long id,patientId,doctorId; private String patientName,doctorName,reason; private LocalDate appointmentDate; private LocalTime timeSlot; private AppointmentStatus status; }
