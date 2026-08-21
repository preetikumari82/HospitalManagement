package com.hospital.Dto;
import java.time.*; import lombok.*;
@Getter @Setter public class AppointmentRequest { private Long patientId; private Long doctorId; private LocalDate appointmentDate; private LocalTime timeSlot; private String reason; }
