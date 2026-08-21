package com.hospital.service;

import com.hospital.Dto.*;
import java.util.List;

public interface IpdService {
    List<BedResponse> beds();
    BedResponse createBed(BedRequest request);
    BedResponse updateBed(Long id, BedRequest request);
    void deleteBed(Long id);
    AdmissionResponse admit(AdmissionRequest request);
    AdmissionResponse discharge(Long id);
    List<AdmissionResponse> activeAdmissions();
    List<AdmissionResponse> patientAdmissions(Long patientId);
}
