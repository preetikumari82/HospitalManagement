package com.hospital.Dto;

import com.hospital.enums.Specialization;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorRegisterRequestDto {

    private String name;
     
    private String email;
    private String password;

    private int age;
    private long salary;
    
    private String phone;
    private Specialization specialization;
}