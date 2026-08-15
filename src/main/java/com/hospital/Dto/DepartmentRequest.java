package com.hospital.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {

    private String name;

    private String description;

    private Boolean active;
}