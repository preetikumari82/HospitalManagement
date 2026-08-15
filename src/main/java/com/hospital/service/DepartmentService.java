package com.hospital.service;

import com.hospital.Dto.DepartmentRequest;
import com.hospital.Dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(
            DepartmentRequest request
    );

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentById(Long id);

    DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request
    );

    void deleteDepartment(Long id);
}