package com.hospital.imp;

import com.hospital.Dto.*;
import com.hospital.enums.*;
import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.IpdService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IpdServiceImpl implements IpdService {
    private final BedRepository bedRepo;
    private final AdmissionRepository admissionRepo;
    private final PatientRepository patientRepo;

    public IpdServiceImpl(BedRepository bedRepo, AdmissionRepository admissionRepo, PatientRepository patientRepo) {
        this.bedRepo = bedRepo; this.admissionRepo = admissionRepo; this.patientRepo = patientRepo;
    }

    public List<BedResponse> beds() {
        return bedRepo.findAll().stream().map(this::bedResponse).toList();
    }

    public BedResponse createBed(BedRequest r) {
        if (bedRepo.existsByWardNameAndBedNumber(r.wardName(), r.bedNumber()))
            throw new IllegalArgumentException("Bed already exists");
        return bedResponse(bedRepo.save(Bed.builder().wardName(r.wardName()).bedNumber(r.bedNumber()).build()));
    }

    public BedResponse updateBed(Long id, BedRequest r) {
        Bed b = bedRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bed not found"));
        if (b.getStatus() == BedStatus.OCCUPIED &&
                (!b.getWardName().equals(r.wardName()) || !b.getBedNumber().equals(r.bedNumber())))
            throw new IllegalStateException("Occupied bed cannot be renamed");
        b.setWardName(r.wardName()); b.setBedNumber(r.bedNumber());
        return bedResponse(bedRepo.save(b));
    }

    public void deleteBed(Long id) {
        Bed b = bedRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Bed not found"));
        if (b.getStatus() == BedStatus.OCCUPIED) throw new IllegalStateException("Occupied bed cannot be deleted");
        bedRepo.delete(b);
    }

    @Transactional
    public AdmissionResponse admit(AdmissionRequest r) {
        Patient p = patientRepo.findById(r.patientId()).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        if (admissionRepo.findFirstByPatientIdAndStatus(p.getId(), AdmissionStatus.ACTIVE).isPresent())
            throw new IllegalStateException("Patient is already admitted");
        Bed b = bedRepo.findById(r.bedId()).orElseThrow(() -> new IllegalArgumentException("Bed not found"));
        if (b.getStatus() != BedStatus.AVAILABLE) throw new IllegalStateException("Bed is already occupied");

        b.setStatus(BedStatus.OCCUPIED);
        Admission a = Admission.builder().patient(p).bed(b).admittedAt(LocalDateTime.now()).status(AdmissionStatus.ACTIVE).build();
        p.setStatus(PatientStatus.ADMITTED);
        p.setAdmissionDate(java.time.LocalDate.now());
        p.setBedNumber(b.getBedNumber());
        patientRepo.save(p); bedRepo.save(b);
        return response(admissionRepo.save(a));
    }

    @Transactional
    public AdmissionResponse discharge(Long id) {
        Admission a = admissionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Admission not found"));
        if (a.getStatus() == AdmissionStatus.DISCHARGED) return response(a);
        a.setStatus(AdmissionStatus.DISCHARGED); a.setDischargedAt(LocalDateTime.now());
        a.getBed().setStatus(BedStatus.AVAILABLE);
        Patient p = a.getPatient(); p.setStatus(PatientStatus.DISCHARGED); p.setDischargeDate(java.time.LocalDate.now()); p.setBedNumber(null);
        patientRepo.save(p); bedRepo.save(a.getBed());
        return response(admissionRepo.save(a));
    }

    public List<AdmissionResponse> activeAdmissions() {
        return admissionRepo.findByStatus(AdmissionStatus.ACTIVE).stream().map(this::response).toList();
    }

    public List<AdmissionResponse> patientAdmissions(Long id) {
        return admissionRepo.findByPatientIdOrderByAdmittedAtDesc(id).stream().map(this::response).toList();
    }

    private BedResponse bedResponse(Bed b) { return new BedResponse(b.getId(), b.getWardName(), b.getBedNumber(), b.getStatus()); }
    private AdmissionResponse response(Admission a) {
        return new AdmissionResponse(a.getId(), a.getPatient().getId(), a.getBed().getId(),
                a.getBed().getWardName(), a.getBed().getBedNumber(), a.getAdmittedAt(), a.getDischargedAt(), a.getStatus());
    }
}
