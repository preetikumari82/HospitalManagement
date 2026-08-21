package com.hospital.Dto;

import com.hospital.enums.Specialization;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorResponse {

    private Long doctorId;
    private String name;
    private String email;
    private int age;
    private long salary;
    private String phone;
    private Specialization specialization;
    private String qualification;
    private double consultationFee;
    private Long departmentId;
    private String departmentName;
}