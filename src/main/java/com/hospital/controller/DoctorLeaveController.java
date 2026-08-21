package com.hospital.controller;

import com.hospital.Dto.DoctorLeaveRequest;
import com.hospital.Dto.DoctorLeaveResponse;
import com.hospital.service.DoctorLeaveService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-leaves")
@CrossOrigin(origins = "*")
public class DoctorLeaveController {

    private final DoctorLeaveService doctorLeaveService;

    public DoctorLeaveController(
            DoctorLeaveService doctorLeaveService) {

        this.doctorLeaveService = doctorLeaveService;
    }

    // ============================
    // CREATE LEAVE
    // ============================

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<DoctorLeaveResponse> createLeave(
            @RequestBody DoctorLeaveRequest request) {

        return new ResponseEntity<>(
                doctorLeaveService.createLeave(request),
                HttpStatus.CREATED
        );
    }

    // ============================
    // GET ALL LEAVES
    // ============================

    @GetMapping
    public ResponseEntity<List<DoctorLeaveResponse>>
    getAllLeaves() {

        return ResponseEntity.ok(
                doctorLeaveService.getAllLeaves()
        );
    }

    // ============================
    // GET LEAVE BY ID
    // ============================

    @GetMapping("/{id}")
    public ResponseEntity<DoctorLeaveResponse>
    getLeaveById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                doctorLeaveService.getLeaveById(id)
        );
    }

    // ============================
    // GET LEAVES BY DOCTOR
    // ============================

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorLeaveResponse>>
    getLeavesByDoctor(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                doctorLeaveService
                        .getLeavesByDoctor(doctorId)
        );
    }

    // ============================
    // UPDATE LEAVE
    // ============================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<DoctorLeaveResponse>
    updateLeave(
            @PathVariable Long id,
            @RequestBody DoctorLeaveRequest request) {

        return ResponseEntity.ok(
                doctorLeaveService.updateLeave(
                        id,
                        request
                )
        );
    }

    // ============================
    // DELETE LEAVE
    // ============================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public ResponseEntity<String> deleteLeave(
            @PathVariable Long id) {

        doctorLeaveService.deleteLeave(id);

        return ResponseEntity.ok(
                "Doctor leave deleted successfully"
        );
    }
}