package com.hospital.service;

import java.util.List;

import com.hospital.Dto.MedicineItemResponse;
import com.hospital.Dto.MedicineRateUpdateRequest;
import com.hospital.Dto.MedicineRequest;
import com.hospital.Dto.MedicineResponse;
import com.hospital.Dto.PatientResponse;

public interface MedicalService {

    // Patient details only (read-only for Medical staff)
    List<PatientResponse> getAllPatients();

    PatientResponse getPatientById(Long id);

    // Give medicine(s) to a patient with rate -> total auto calculated per item
    MedicineResponse addMedicine(Long patientId, MedicineRequest request);

    // Update rate (and/or quantity) of one medicine line -> total auto recalculated
    MedicineItemResponse updateMedicineRate(Long medicineId, MedicineRateUpdateRequest request);

    // All medicines for a patient with the grand total auto summed
    MedicineResponse getMedicineBill(Long patientId);

    // Printable receipt: patient details + medicines + rate + auto-summed total
    String generateReceipt(Long patientId);
}
