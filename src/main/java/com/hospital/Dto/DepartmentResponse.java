package com.hospital.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DepartmentResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean active;

    public DepartmentResponse() {
    }

}