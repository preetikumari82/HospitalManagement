package com.hospital.Dto;
import lombok.*; @Getter @Setter @Builder public class PrescriptionResponse { private Long id,medicalRecordId; private String medicineName,dosage,duration,instructions; }
