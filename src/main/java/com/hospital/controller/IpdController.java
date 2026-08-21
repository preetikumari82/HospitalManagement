package com.hospital.controller;

import com.hospital.Dto.*;
import com.hospital.service.IpdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class IpdController {
    private final IpdService service;
    public IpdController(IpdService service) { this.service = service; }

    @GetMapping("/beds")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
    public List<BedResponse> beds() { return service.beds(); }

    @PostMapping("/beds")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ResponseEntity<BedResponse> createBed(@Valid @RequestBody BedRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBed(request));
    }

    @PutMapping("/beds/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public BedResponse updateBed(@PathVariable Long id, @Valid @RequestBody BedRequest request) {
        return service.updateBed(id, request);
    }

    @DeleteMapping("/beds/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBed(@PathVariable Long id) { service.deleteBed(id); }

    @PostMapping("/admissions")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    public ResponseEntity<AdmissionResponse> admit(@Valid @RequestBody AdmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.admit(request));
    }

    @PutMapping("/admissions/{id}/discharge")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
    public AdmissionResponse discharge(@PathVariable Long id) { return service.discharge(id); }

    @GetMapping("/admissions/active")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE')")
    public List<AdmissionResponse> active() { return service.activeAdmissions(); }

    @GetMapping("/admissions/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PATIENT')")
    public List<AdmissionResponse> patient(@PathVariable Long patientId) { return service.patientAdmissions(patientId); }
}
