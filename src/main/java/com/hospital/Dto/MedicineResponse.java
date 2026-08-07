package com.hospital.Dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MedicineResponse {

    private Long patientId;

    private String patient;

    private String doctor;

    private LocalDate date;

    private List<MedicineItemResponse> medicines;

    // Auto summed total of ALL medicines currently on the patient's record
    private Double grandTotal;
}
