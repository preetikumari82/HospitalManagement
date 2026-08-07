package com.hospital.Dto;

import java.time.LocalDate;

import com.hospital.enums.PatientStatus;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {

    private Long id;

    private String name;
    private String email;

    private int age;
    private String gender;
    private String phone;
    private String address;

    private long fees;

    private String disease;

    private PatientStatus status;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private String role;
    private String treatment;
    private String medicine;
    private String nurseRemarks;
}