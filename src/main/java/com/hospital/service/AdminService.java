package com.hospital.service;


import java.util.List;
import java.util.Map;

import com.hospital.Dto.DoctorRegisterRequestDto;
import com.hospital.Dto.DoctorResponse;
import com.hospital.Dto.MedicalRegisterRequestDto;
import com.hospital.Dto.MedicalResponse;
import com.hospital.Dto.StaffAccountResponse;
import com.hospital.Dto.StaffAccountRequest;

public interface AdminService {

	DoctorResponse registerDoctor(DoctorRegisterRequestDto request);

	List<DoctorResponse> getAlldoctor();

	DoctorResponse getDoctorById(Long id);

	DoctorResponse updateDoctor(Long id, DoctorRegisterRequestDto request);

	void deleteDoctor(Long id);

	// Medical (pharmacy/billing) staff management
	MedicalResponse registerMedical(MedicalRegisterRequestDto request);

	List<MedicalResponse> getAllMedical();

	MedicalResponse getMedicalById(Long id);

	MedicalResponse updateMedical(Long id, MedicalRegisterRequestDto request);

	void deleteMedical(Long id);

	// ===== FR1.3: generic staff account management (works across staff types) =====
	List<StaffAccountResponse> getAllStaffAccounts();

	StaffAccountResponse deactivateStaffAccount(Long userId);

	StaffAccountResponse activateStaffAccount(Long userId);
    StaffAccountResponse createStaffAccount(StaffAccountRequest request);
    StaffAccountResponse updateStaffAccount(Long userId, StaffAccountRequest request);

}