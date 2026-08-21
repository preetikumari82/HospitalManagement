package com.hospital.service;

import com.hospital.Dto.DoctorLeaveRequest;
import com.hospital.Dto.DoctorLeaveResponse;

import java.util.List;

public interface DoctorLeaveService {

    // Apply leave
    DoctorLeaveResponse createLeave(
            DoctorLeaveRequest request
    );

    // Get all leaves
    List<DoctorLeaveResponse> getAllLeaves();

    // Get leave by ID
    DoctorLeaveResponse getLeaveById(Long id);

    // Get leaves of particular doctor
    List<DoctorLeaveResponse> getLeavesByDoctor(
            Long doctorId
    );

    // Update leave
    DoctorLeaveResponse updateLeave(
            Long id,
            DoctorLeaveRequest request
    );

    // Delete leave
    void deleteLeave(Long id);
}