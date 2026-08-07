package com.hospital.service;


import java.util.List;
import java.util.Map;

import com.hospital.Dto.DoctorRegisterRequestDto;
import com.hospital.Dto.DoctorResponse;
import com.hospital.Dto.MedicalRegisterRequestDto;
import com.hospital.Dto.MedicalResponse;

public interface AdminService {

	DoctorResponse registerDoctor(DoctorRegisterRequestDto request);

	List<DoctorResponse> getAlldoctor();

	DoctorResponse getDoctorById(Long id);

	DoctorResponse updateDoctor(Long id, DoctorRegisterRequestDto request);

	// Medical (pharmacy/billing) staff management
	MedicalResponse registerMedical(MedicalRegisterRequestDto request);

	List<MedicalResponse> getAllMedical();

	MedicalResponse getMedicalById(Long id);

	MedicalResponse updateMedical(Long id, MedicalRegisterRequestDto request);

	void deleteMedical(Long id);

}