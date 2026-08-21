package com.hospital.Dto;
import lombok.*; @Getter @Setter public class MedicalRecordRequest { private Long patientId; private Long doctorId; private Long appointmentId; private String diagnosis; private String notes; }
