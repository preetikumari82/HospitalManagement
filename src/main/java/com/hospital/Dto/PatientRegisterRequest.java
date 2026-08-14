package com.hospital.Dto;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class PatientRegisterRequest {
    @NotBlank private String name;
    @NotBlank @Email private String email;
    @NotBlank @Size(min=6) private String password;
    @Min(1) @Max(120) private int age;
    @NotBlank private String gender;
    @NotBlank @Pattern(regexp="^[6-9]\\d{9}$") private String phone;
    @NotBlank private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
}
