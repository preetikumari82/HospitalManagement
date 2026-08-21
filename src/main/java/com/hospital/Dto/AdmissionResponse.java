package com.hospital.Dto;
import com.hospital.enums.AdmissionStatus;
import java.time.LocalDateTime;
public record AdmissionResponse(Long id, Long patientId, Long bedId, String wardName, String bedNumber,
                                 LocalDateTime admittedAt, LocalDateTime dischargedAt, AdmissionStatus status) {}
