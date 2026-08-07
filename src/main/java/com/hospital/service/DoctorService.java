package com.hospital.service;

import java.util.List;

import com.hospital.Dto.NurseRequest;
import com.hospital.Dto.NurseResponse;
import com.hospital.Dto.PatientRequest;
import com.hospital.Dto.PatientResponse;

public interface DoctorService {

    PatientResponse admitPatient(PatientRequest request);
    List<PatientResponse> getAllPatients();
	PatientResponse getPatientById(Long id);
	PatientResponse updatePatient(Long id, PatientRequest request);
	PatientResponse dischargePatient(Long id);
	
	NurseResponse createNurse(NurseRequest request);
	List<NurseResponse> getAllNurses();
	
	
	NurseResponse getNurseById(Long id);
	
	NurseResponse updateNurse(Long id, NurseRequest request);
	String deleteNurse(Long id);



}