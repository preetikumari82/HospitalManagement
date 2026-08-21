package com.hospital.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DoctorLeaveRequest {

    private Long doctorId;

    private LocalDate leaveDate;

    private String reason;

    private Boolean approved;
}