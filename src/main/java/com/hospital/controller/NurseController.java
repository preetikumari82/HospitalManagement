package com.hospital.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hospital.Dto.MedicineRequest;
import com.hospital.Dto.MedicineResponse;
import com.hospital.Dto.PatientRequest;
import com.hospital.Dto.PatientResponse;
import com.hospital.Dto.ReceiptResponse;
import com.hospital.Dto.StatusRequest;
import com.hospital.Dto.TreatmentRequest;
import com.hospital.model.Medicine;
import com.hospital.service.NurseService;

@RestController
@RequestMapping("/api/nurse")
@PreAuthorize("hasRole('NURSE')") // FR1.2
public class NurseController {

    private final NurseService nurseService;

    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }


    // 1. Get All Patients
    @GetMapping("/allPatients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        return ResponseEntity.ok(
                nurseService.getAllPatients()
        );
    }


    // 2. Get Patient By Id
    @GetMapping("/getPatientById/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                nurseService.getPatientById(id)
        );
    }


    @PutMapping("/updateTreatment/{id}")
    public ResponseEntity<PatientResponse> updateTreatment(
            @PathVariable Long id,
            @RequestBody TreatmentRequest request) {

        return ResponseEntity.ok(
                nurseService.updateTreatment(id, request));
    }


    // 4. Update Patient Status
    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<PatientResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest request) {

        return ResponseEntity.ok(
                nurseService.updateStatus(id, request)
        );
    }
    @PostMapping("/createReceipt/{patientId}")
    public ResponseEntity<Object> createReceipt(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                nurseService.createReceipt(patientId));
    }

    // Add Medicine
    @PostMapping("/addMedicine/{patientId}")
    public ResponseEntity<MedicineResponse> addMedicine(
            @PathVariable Long patientId,
            @RequestBody MedicineRequest request) {

        return ResponseEntity.ok(
                nurseService.addMedicine(patientId, request)
        );
    }

   

}