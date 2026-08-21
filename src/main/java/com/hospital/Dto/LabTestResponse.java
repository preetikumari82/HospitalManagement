package com.hospital.Dto;
import java.time.*; import com.hospital.enums.LabTestStatus; import lombok.*; @Getter @Setter @Builder public class LabTestResponse { private Long id,patientId,doctorId,medicalRecordId; private String patientName,doctorName,testName,resultValue,resultFileUrl; private LabTestStatus status; private LocalDateTime requestedAt,completedAt; }
