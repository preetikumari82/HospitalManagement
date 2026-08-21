package com.hospital.Dto;
import jakarta.validation.constraints.NotNull;
public record AdmissionRequest(@NotNull Long patientId, @NotNull Long bedId) {}
