package com.hospital.service;
import java.util.List;
import com.hospital.Dto.*;
public interface PatientService {
    PatientResponse selfRegister(PatientRegisterRequest request);
    List<PatientResponse> getAll(String q, String status);
    PatientResponse getById(Long id);
    PatientResponse getByEmail(String email);
    PatientResponse update(Long id, PatientRequest request);
    PatientResponse admit(Long id, PatientRequest request);
    PatientResponse discharge(Long id);
}
