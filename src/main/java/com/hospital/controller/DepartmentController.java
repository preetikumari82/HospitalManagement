package com.hospital.controller;

import com.hospital.Dto.DepartmentRequest;
import com.hospital.Dto.DepartmentResponse;
import com.hospital.service.DepartmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService) {

        this.departmentService = departmentService;
    }


    // =====================================
    // CREATE DEPARTMENT
    // ADMIN ONLY
    // =====================================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse>
    createDepartment(
            @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.createDepartment(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // =====================================
    // GET ALL DEPARTMENTS
    // =====================================

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>>
    getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments()
        );
    }


    // =====================================
    // GET DEPARTMENT BY ID
    // =====================================

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse>
    getDepartmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id)
        );
    }


    // =====================================
    // UPDATE DEPARTMENT
    // ADMIN ONLY
    // =====================================

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse>
    updateDepartment(
            @PathVariable Long id,
            @RequestBody DepartmentRequest request) {

        DepartmentResponse response =
                departmentService.updateDepartment(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =====================================
    // DELETE DEPARTMENT
    // ADMIN ONLY
    // =====================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String>
    deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.ok(
                "Department deleted successfully"
        );
    }
}