package com.hospital.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NurseResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String gender;

    private int experience;

    private String shift;

    private Long doctorId;

    private String role;
}