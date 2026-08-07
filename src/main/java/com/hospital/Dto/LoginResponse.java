package com.hospital.Dto;

import com.hospital.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String message;
    private String name;
    private String email;
    private String token;
    private Role role;

    // The id of the role-specific profile row (Doctor.id / Nurse.id / Medical.id / Patient.id)
    // for the logged-in user, so the frontend does not have to guess it. Null for ADMIN.
    private Long profileId;
}