package com.hospital.Dto;

import com.hospital.enums.PatientStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequest {

    private PatientStatus status;

}