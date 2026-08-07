package com.hospital.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.Dto.MedicineItemResponse;
import com.hospital.Dto.MedicineRateUpdateRequest;
import com.hospital.Dto.MedicineRequest;
import com.hospital.Dto.MedicineResponse;
import com.hospital.Dto.PatientResponse;
import com.hospital.service.MedicalService;

@RestController
@RequestMapping("/api/medical")
public class MedicalController {

    private final MedicalService medicalService;

    public MedicalController(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    // 1. Get all patients (patient details only, for Medical staff)
    @GetMapping("/allPatients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {

        return ResponseEntity.ok(
                medicalService.getAllPatients());
    }

    // 2. Get patient by id
    @GetMapping("/getPatientById/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                medicalService.getPatientById(id));
    }

    // 3. Give medicine(s) to a patient with rate -> total auto calculated
    @PostMapping("/addMedicine/{patientId}")
    public ResponseEntity<MedicineResponse> addMedicine(
            @PathVariable Long patientId,
            @RequestBody MedicineRequest request) {

        return ResponseEntity.ok(
                medicalService.addMedicine(patientId, request));
    }

    // 4. Update the rate (and/or quantity) of one medicine -> total auto recalculated
    @PutMapping("/updateMedicineRate/{medicineId}")
    public ResponseEntity<MedicineItemResponse> updateMedicineRate(
            @PathVariable Long medicineId,
            @RequestBody MedicineRateUpdateRequest request) {

        return ResponseEntity.ok(
                medicalService.updateMedicineRate(medicineId, request));
    }

    // 5. All medicines of a patient with the grand total auto summed
    @GetMapping("/medicineBill/{patientId}")
    public ResponseEntity<MedicineResponse> getMedicineBill(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalService.getMedicineBill(patientId));
    }

    // 6. Printable receipt: patient details + medicines + rate + auto-summed total
    @PostMapping("/generateReceipt/{patientId}")
    public ResponseEntity<String> generateReceipt(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalService.generateReceipt(patientId));
    }
}
