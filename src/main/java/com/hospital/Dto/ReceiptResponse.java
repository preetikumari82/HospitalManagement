package com.hospital.Dto;

import java.time.LocalDate;

import com.hospital.enums.PatientStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReceiptResponse {

    private Long patientId;

    private String patientName;

    private String disease;

    private String treatment;

    private String medicine;

    private String nurseRemarks;

    private PatientStatus status;

    private long fees;

    private LocalDate admissionDate;

    private LocalDate receiptDate;
}