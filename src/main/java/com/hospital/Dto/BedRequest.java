package com.hospital.Dto;
import jakarta.validation.constraints.NotBlank;
public record BedRequest(@NotBlank String wardName, @NotBlank String bedNumber) {}
