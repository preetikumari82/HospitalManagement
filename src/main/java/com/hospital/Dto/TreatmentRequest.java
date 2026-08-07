package com.hospital.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TreatmentRequest {

    private String treatment;

    private String medicine;

    private String nurseRemarks;
}