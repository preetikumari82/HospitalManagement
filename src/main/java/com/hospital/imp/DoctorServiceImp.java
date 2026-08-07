package com.hospital.imp;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.Dto.NurseRequest;
import com.hospital.Dto.NurseResponse;
import com.hospital.Dto.PatientRequest;
import com.hospital.Dto.PatientResponse;
import com.hospital.enums.PatientStatus;
import com.hospital.enums.Role;
import com.hospital.model.Doctor;
import com.hospital.model.Nurse;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.NurseRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import com.hospital.service.DoctorService;

@Service
public  class DoctorServiceImp implements DoctorService {

	private PatientRepository patientRepository;
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private DocterRepository doctorRepository;
	private NurseRepository nurseRepository;


	public DoctorServiceImp(
	        PatientRepository patientRepository,
	        UserRepository userRepository,
	        PasswordEncoder passwordEncoder,
	        DocterRepository doctorRepository,
	        NurseRepository nurseRepository) {

	    this.patientRepository = patientRepository;
	    this.userRepository = userRepository;
	    this.passwordEncoder = passwordEncoder;
	    this.doctorRepository = doctorRepository;
	    this.nurseRepository = nurseRepository;
	}


	@Override
	public PatientResponse admitPatient(PatientRequest request) {

	    // Normalize email
	    String email = request.getEmail().trim().toLowerCase();
String  name=request.getName().trim();
	    // Check duplicate email
	    if (userRepository.existsByEmail(email)) {
	        throw new RuntimeException("Email already exists.");
	    }

	    // Create User
	    User user = new User();
	   user.setName(name);
	    user.setEmail(email);
	    user.setPassword(passwordEncoder.encode(request.getPassword()));
	    user.setRole(Role.PATIENT);

	    User savedUser = userRepository.save(user);

	    // Create Patient
	    Patient patient = new Patient();
	    patient.setUser(savedUser);
	    patient.setStatus(PatientStatus.ADMITTED);
	    patient.setAdmissionDate(LocalDate.now());

	    patient.setAge(request.getAge());
	    patient.setGender(request.getGender());
	    patient.setPhone(request.getPhone());
	    patient.setAddress(request.getAddress());
	    patient.setFees(request.getFees());
	    patient.setDisease(request.getDisease());

	    Patient savedPatient = patientRepository.save(patient);

	    return PatientResponse.builder()
	            .id(savedPatient.getId())
	            .name(savedPatient.getUser().getName())
	            .email(savedPatient.getUser().getEmail())
	            .age(savedPatient.getAge())
	            .gender(savedPatient.getGender())
	            .phone(savedPatient.getPhone())
	            .address(savedPatient.getAddress())
	            .fees(savedPatient.getFees())
	            .disease(savedPatient.getDisease())
	            .role(savedPatient.getUser().getRole().name())
	            .build();
	}
    @Override
    public List<PatientResponse> getAllPatients() {

        return patientRepository.findAll()
                .stream()
                .map(patient -> {

                    PatientResponse response = new PatientResponse();

                    response.setId(patient.getId());
                    response.setAge(patient.getAge());
                    response.setGender(patient.getGender());
                    response.setPhone(patient.getPhone());
                    response.setAddress(patient.getAddress());
                    response.setFees(patient.getFees());
                    response.setDisease(patient.getDisease());

                    response.setName(patient.getUser().getName());
                    response.setEmail(patient.getUser().getEmail());
                    response.setRole(patient.getUser().getRole().name());

                    return response;

                })
                .toList();
    }
    @Override
    public PatientResponse getPatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id : " + id));

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .age(patient.getAge())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .fees(patient.getFees())
                .disease(patient.getDisease())
                .status(patient.getStatus())
                .admissionDate(patient.getAdmissionDate())
                .dischargeDate(patient.getDischargeDate())
                .role(patient.getUser().getRole().name())
                .build();
    }
    @Override
    public PatientResponse updatePatient(Long id, PatientRequest request) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id : " + id));


        // Patient details update
       patient.getUser().getName();
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setFees(request.getFees());
        patient.setDisease(request.getDisease());


        Patient updatedPatient = patientRepository.save(patient);


        return PatientResponse.builder()
                .id(updatedPatient.getId())
                .name(updatedPatient.getUser().getName())
                .email(updatedPatient.getUser().getEmail())
                .age(updatedPatient.getAge())
                .gender(updatedPatient.getGender())
                .phone(updatedPatient.getPhone())
                .address(updatedPatient.getAddress())
                .fees(updatedPatient.getFees())
                .disease(updatedPatient.getDisease())
                .role(updatedPatient.getUser().getRole().name())
                .build();
    }
    @Override
    public PatientResponse dischargePatient(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found with id: " ));

        // Update patient
        patient.getStatus();
        patient.setDischargeDate(LocalDate.now());
        patient.setStatus(PatientStatus.DISCHARGED);

        Patient dischargedPatient = patientRepository.save(patient);
        

        return PatientResponse.builder()
                .id(dischargedPatient.getId())
                .name(dischargedPatient.getUser().getName())
                .email(dischargedPatient.getUser().getEmail())
                .age(dischargedPatient.getAge())
                .gender(dischargedPatient.getGender())
                .phone(dischargedPatient.getPhone())
                .address(dischargedPatient.getAddress())
                .fees(dischargedPatient.getFees())
                .disease(dischargedPatient.getDisease())
                .status(dischargedPatient.getStatus())
                .admissionDate(dischargedPatient.getAdmissionDate())
                .dischargeDate(dischargedPatient.getDischargeDate())
                
                .role(dischargedPatient.getUser().getRole().name())
                .build();
    }
    ///// create nurse by  doctor


    @Override
    public NurseResponse createNurse(NurseRequest request) {

        System.out.println("Step 1");

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        System.out.println("Step 2");

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.NURSE);

        System.out.println("Step 3");

        User savedUser = userRepository.save(user);

        System.out.println("Step 4");

        Nurse nurse = new Nurse();
        nurse.setUser(savedUser);
        nurse.setDoctor(doctor);
        nurse.setPhone(request.getPhone());
        nurse.setGender(request.getGender());
        nurse.setExperience(request.getExperience());
        nurse.setShift(request.getShift());

        Nurse savedNurse = nurseRepository.save(nurse);

        System.out.println("Step 5");

        return NurseResponse.builder()
                .id(savedNurse.getId())
                .name(savedNurse.getUser().getName())
                .email(savedNurse.getUser().getEmail())
                .phone(savedNurse.getPhone())
                .gender(savedNurse.getGender())
                .experience(savedNurse.getExperience())
                .shift(savedNurse.getShift())
                .doctorId(savedNurse.getDoctor().getId())
                .role(savedNurse.getUser().getRole().name())
                .build();
    }
    /// get all nurse 
    @Override
    public List<NurseResponse> getAllNurses() {

        return nurseRepository.findAll()
                .stream()
                .map(nurse -> NurseResponse.builder()
                        .id(nurse.getId())
                        .name(nurse.getUser().getName())
                        .email(nurse.getUser().getEmail())
                        .phone(nurse.getPhone())
                        .gender(nurse.getGender())
                        .experience(nurse.getExperience())
                        .shift(nurse.getShift())
                        .doctorId(nurse.getDoctor().getId())
                        .role(nurse.getUser().getRole().name())
                        .build())
                .toList();
    }
// get nurse by id
    
    @Override
    public NurseResponse getNurseById(Long id) {

        Nurse nurse = nurseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Nurse not found with id : " + id));

        return NurseResponse.builder()
                .id(nurse.getId())
                .name(nurse.getUser().getName())
                .email(nurse.getUser().getEmail())
                .phone(nurse.getPhone())
                .gender(nurse.getGender())
                .experience(nurse.getExperience())
                .shift(nurse.getShift())
                .doctorId(nurse.getDoctor().getId())
                .role(nurse.getUser().getRole().name())
                .build();
    }
    //update nurse details
    @Override
    public NurseResponse updateNurse(Long id, NurseRequest request) {

        Nurse nurse = nurseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Nurse not found with id : " + id));

        // Update User Details
        nurse.getUser().setName(request.getName());
        nurse.getUser().setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            nurse.getUser().setPassword(
                    passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(nurse.getUser());

        // Update Nurse Details
        nurse.setPhone(request.getPhone());
        nurse.setGender(request.getGender());
        nurse.setExperience(request.getExperience());
        nurse.setShift(request.getShift());

        // Update Doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        nurse.setDoctor(doctor);

        Nurse updatedNurse = nurseRepository.save(nurse);

        return NurseResponse.builder()
                .id(updatedNurse.getId())
                .name(updatedNurse.getUser().getName())
                .email(updatedNurse.getUser().getEmail())
                .phone(updatedNurse.getPhone())
                .gender(updatedNurse.getGender())
                .experience(updatedNurse.getExperience())
                .shift(updatedNurse.getShift())
                .doctorId(updatedNurse.getDoctor().getId())
                .role(updatedNurse.getUser().getRole().name())
                .build();
    }
    @Override
    public String deleteNurse(Long id) {

        Nurse nurse = nurseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Nurse not found with id : " + id));

        nurseRepository.delete(nurse);

        return "Nurse deleted successfully";
    }
   
    
    
	


	
    
    
    
   
   
    
    
}