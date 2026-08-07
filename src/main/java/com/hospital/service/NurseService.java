package com.hospital.service;

import java.util.List;

import com.hospital.Dto.MedicineRequest;
import com.hospital.Dto.MedicineResponse;
import com.hospital.Dto.PatientRequest;
import com.hospital.Dto.PatientResponse;
import com.hospital.Dto.StatusRequest;
import com.hospital.Dto.TreatmentRequest;
import com.hospital.model.Medicine;

public interface NurseService {

    List<PatientResponse> getAllPatients();

    PatientResponse getPatientById(Long id);

    PatientResponse updateTreatment(Long id, TreatmentRequest request);

    PatientResponse updateStatus(Long id, StatusRequest request);

	



	MedicineResponse addMedicine(Long patientId, MedicineRequest request);

	Object createReceipt(Long patientId);
}