package com.hospital.Dto;
import com.hospital.enums.BedStatus;
public record BedResponse(Long id, String wardName, String bedNumber, BedStatus status) {}
