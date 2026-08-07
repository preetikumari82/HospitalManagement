package com.hospital.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String gender;

    private String department;

    private String role;
}
