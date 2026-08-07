package com.hospital.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NurseRequest {

    private String name;

    private String email;

    private String password;

    private String phone;

    private String gender;

    private int experience;

    private String shift;

    private Long doctorId;

}