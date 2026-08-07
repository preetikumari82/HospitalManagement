package com.hospital.Dto;

import com.hospital.enums.PatientStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 120, message = "Age must be less than 120")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
             message = "Phone number must be 10 digits")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @Min(value = 1, message = "Fees must be greater than 0")
    private long fees;

    @NotBlank(message = "Disease is required")
    private String disease;

    @NotNull(message = "Doctor Id is required")
    private Long doctorId;

    private String treatment;

    private String medicine;

    private String nurseRemarks;

    private PatientStatus status;

    private Double temperature;

    private String bloodPressure;

    private Integer pulseRate;

    private Integer oxygenLevel;
}