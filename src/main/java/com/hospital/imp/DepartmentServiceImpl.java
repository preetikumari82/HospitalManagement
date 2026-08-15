package com.hospital.imp;

import com.hospital.Dto.DepartmentRequest;
import com.hospital.Dto.DepartmentResponse;
import com.hospital.model.Department;
import com.hospital.repository.DepartmentRepository;
import com.hospital.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository) {

        this.departmentRepository = departmentRepository;
    }

    // =========================
    // CREATE DEPARTMENT
    // =========================

    @Override
    public DepartmentResponse createDepartment(
            DepartmentRequest request) {

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Department name is required"
            );
        }

        String name = request.getName().trim();

        // Check duplicate department
        if (departmentRepository.existsByNameIgnoreCase(name)) {

            throw new RuntimeException(
                    "Department already exists"
            );
        }

        Department department = new Department();

        department.setName(name);

        department.setDescription(
                request.getDescription()
        );

        // Default active = true
        if (request.getActive() != null) {
            department.setActive(
                    request.getActive()
            );
        } else {
            department.setActive(true);
        }

        Department saved =
                departmentRepository.save(department);

        return convertToResponse(saved);
    }


    // =========================
    // GET ALL DEPARTMENTS
    // =========================

    @Override
    public List<DepartmentResponse> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =========================
    // GET DEPARTMENT BY ID
    // =========================

    @Override
    public DepartmentResponse getDepartmentById(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found with id: "
                                                + id
                                )
                        );

        return convertToResponse(department);
    }


    // =========================
    // UPDATE DEPARTMENT
    // =========================

    @Override
    public DepartmentResponse updateDepartment(
            Long id,
            DepartmentRequest request) {

        Department existingDepartment =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found with id: "
                                                + id
                                )
                        );

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Department name is required"
            );
        }

        String newName =
                request.getName().trim();

        // Check duplicate name
        if (!existingDepartment
                .getName()
                .equalsIgnoreCase(newName)
                &&
                departmentRepository
                        .existsByNameIgnoreCase(newName)) {

            throw new RuntimeException(
                    "Department with this name already exists"
            );
        }

        existingDepartment.setName(newName);

        existingDepartment.setDescription(
                request.getDescription()
        );

        if (request.getActive() != null) {

            existingDepartment.setActive(
                    request.getActive()
            );
        }

        Department updated =
                departmentRepository.save(
                        existingDepartment
                );

        return convertToResponse(updated);
    }


    // =========================
    // DELETE DEPARTMENT
    // =========================

    @Override
    public void deleteDepartment(Long id) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found with id: "
                                                + id
                                )
                        );

        departmentRepository.delete(department);
    }


    // =========================
    // ENTITY → RESPONSE
    // =========================

    private DepartmentResponse convertToResponse(
            Department department) {

        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getDescription(),
                department.isActive()
        );
    }
}