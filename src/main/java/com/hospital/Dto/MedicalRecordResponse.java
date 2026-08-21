package com.hospital.Dto;
import java.time.*; import lombok.*; @Getter @Setter @Builder public class MedicalRecordResponse { private Long id,patientId,doctorId,appointmentId; private String patientName,doctorName,diagnosis,notes; private LocalDateTime createdAt; }
