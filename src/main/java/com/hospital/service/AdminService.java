package com.hospital.service;


import java.util.List;
import java.util.Map;

import com.hospital.Dto.DoctorRegisterRequestDto;
import com.hospital.Dto.DoctorResponse;

public interface AdminService {

	DoctorResponse registerDoctor(DoctorRegisterRequestDto request);

	List<DoctorResponse> getAlldoctor();

	DoctorResponse getDoctorById(Long id);

	DoctorResponse updateDoctor(Long id, DoctorRegisterRequestDto request);



	
}