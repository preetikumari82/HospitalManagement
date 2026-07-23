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
    private Role role;
}