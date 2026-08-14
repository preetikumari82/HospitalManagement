package com.hospital.imp;

import java.time.LocalDate;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.hospital.Dto.*;
import com.hospital.enums.*;
import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final DocterRepository doctorRepository;

    public PatientServiceImpl(UserRepository u, PatientRepository p, PasswordEncoder e, DocterRepository d) {
        userRepository=u; patientRepository=p; passwordEncoder=e; doctorRepository=d;
    }

    public PatientResponse selfRegister(PatientRegisterRequest r) {
        if (userRepository.existsByEmail(r.getEmail())) throw new RuntimeException("Email already registered");
        User u=new User(); u.setName(r.getName()); u.setEmail(r.getEmail()); u.setUsername(r.getEmail());
        u.setPassword(passwordEncoder.encode(r.getPassword())); u.setRole(Role.PATIENT); u.setActive(true);
        Patient p=Patient.builder().user(userRepository.save(u)).age(r.getAge()).gender(r.getGender())
            .phone(r.getPhone()).address(r.getAddress()).status(PatientStatus.REFERRED)
            .encounterType(EncounterType.OPD).emergencyContactName(r.getEmergencyContactName())
            .emergencyContactPhone(r.getEmergencyContactPhone()).emergencyContactRelationship(r.getEmergencyContactRelationship())
            .build();
        return toResponse(patientRepository.save(p));
    }

    public List<PatientResponse> getAll(String q, String status) {
        List<Patient> list;
        if (status != null && !status.isBlank()) list=patientRepository.findByStatus(PatientStatus.valueOf(status.toUpperCase()));
        else list=patientRepository.findAll();
        if (q != null && !q.isBlank()) {
            String s=q.toLowerCase();
            list=list.stream().filter(p -> String.valueOf(p.getId()).equals(q)
                || (p.getUser()!=null && p.getUser().getName()!=null && p.getUser().getName().toLowerCase().contains(s))
                || (p.getPhone()!=null && p.getPhone().contains(q))).toList();
        }
        return list.stream().map(this::toResponse).toList();
    }

    public PatientResponse getById(Long id) {
        return toResponse(patientRepository.findById(id).orElseThrow(()->new RuntimeException("Patient not found")));
    }

    public PatientResponse getByEmail(String email) {
        User u=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("Patient account not found"));
        Patient p=patientRepository.findByUserId(u.getId()).orElseThrow(()->new RuntimeException("Patient profile not found"));
        return toResponse(p);
    }

    public PatientResponse update(Long id, PatientRequest r) {
        Patient p=patientRepository.findById(id).orElseThrow(()->new RuntimeException("Patient not found"));
        apply(p,r); return toResponse(patientRepository.save(p));
    }

    public PatientResponse admit(Long id, PatientRequest r) {
        Patient p=patientRepository.findById(id).orElseThrow(()->new RuntimeException("Patient not found"));
        if (r.getDoctorId()!=null) p.setDoctor(doctorRepository.findById(r.getDoctorId()).orElseThrow(()->new RuntimeException("Doctor not found")));
        p.setEncounterType(EncounterType.IPD); p.setStatus(PatientStatus.ADMITTED); p.setAdmissionDate(LocalDate.now());
        if (r.getBedNumber()!=null) p.setBedNumber(r.getBedNumber());
        applyCommon(p,r);
        return toResponse(patientRepository.save(p));
    }

    public PatientResponse discharge(Long id, PatientRequest r) {
        Patient p=patientRepository.findById(id).orElseThrow(()->new RuntimeException("Patient not found"));
        p.setStatus(PatientStatus.DISCHARGED); p.setDischargeDate(LocalDate.now());
        return toResponse(patientRepository.save(p));
    }

    private void apply(Patient p, PatientRequest r) {
        if (r.getDoctorId()!=null) p.setDoctor(doctorRepository.findById(r.getDoctorId()).orElseThrow(()->new RuntimeException("Doctor not found")));
        applyCommon(p,r);
        if (r.getStatus()!=null) p.setStatus(r.getStatus());
        if (r.getEncounterType()!=null) p.setEncounterType(r.getEncounterType());
    }
    private void applyCommon(Patient p, PatientRequest r) {
        User u=p.getUser(); u.setName(r.getName()); u.setEmail(r.getEmail());
        if (r.getPassword()!=null && !r.getPassword().isBlank()) u.setPassword(passwordEncoder.encode(r.getPassword()));
        userRepository.save(u);
        p.setAge(r.getAge()); p.setGender(r.getGender()); p.setPhone(r.getPhone()); p.setAddress(r.getAddress());
        p.setFees(r.getFees()); p.setDisease(r.getDisease()); p.setTreatment(r.getTreatment()); p.setNurseRemarks(r.getNurseRemarks());
        p.setBedNumber(r.getBedNumber()); p.setEmergencyContactName(r.getEmergencyContactName());
        p.setEmergencyContactPhone(r.getEmergencyContactPhone()); p.setEmergencyContactRelationship(r.getEmergencyContactRelationship());
        p.setAllergies(r.getAllergies()); p.setPastConditions(r.getPastConditions()); p.setPrescriptions(r.getPrescriptions());
    }
    private PatientResponse toResponse(Patient p) {
        User u=p.getUser();
        return PatientResponse.builder().id(p.getId()).name(u.getName()).email(u.getEmail()).age(p.getAge()).gender(p.getGender())
          .phone(p.getPhone()).address(p.getAddress()).fees(p.getFees()).disease(p.getDisease()).status(p.getStatus())
          .encounterType(p.getEncounterType()).admissionDate(p.getAdmissionDate()).dischargeDate(p.getDischargeDate()).bedNumber(p.getBedNumber())
          .emergencyContactName(p.getEmergencyContactName()).emergencyContactPhone(p.getEmergencyContactPhone())
          .emergencyContactRelationship(p.getEmergencyContactRelationship()).allergies(p.getAllergies()).pastConditions(p.getPastConditions())
          .prescriptions(p.getPrescriptions()).role(u.getRole().name()).treatment(p.getTreatment()).nurseRemarks(p.getNurseRemarks()).build();
    }

	@Override
	public PatientResponse discharge(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}
