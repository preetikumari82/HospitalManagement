package com.hospital.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorLeaveResponse {

    private Long id;

    private Long doctorId;

    private String doctorName;

    private LocalDate leaveDate;

    private String reason;

    private Boolean approved;

	
}