package com.hospital.Dto;
import lombok.*; @Getter @Setter public class LabTestRequest { private Long patientId; private Long doctorId; private Long medicalRecordId; private String testName; }
