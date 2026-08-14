package com.hospital.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.Dto.DoctorRegisterRequestDto;
import com.hospital.Dto.DoctorResponse;
import com.hospital.Dto.MedicalRegisterRequestDto;
import com.hospital.Dto.MedicalResponse;
import com.hospital.Dto.StaffAccountResponse;
import com.hospital.Dto.StaffAccountRequest;
import com.hospital.enums.Role;
import com.hospital.model.Doctor;
import com.hospital.model.Medical;
import com.hospital.model.User;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.MedicalRepository;
import com.hospital.repository.UserRepository;
import com.hospital.service.AdminService;

@Service
public class AdminServceImp implements AdminService {

    @Autowired
    private DocterRepository doctorRepository;

    @Autowired
    private MedicalRepository medicalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Override
    public DoctorResponse registerDoctor(DoctorRegisterRequestDto request) {

        // 1. Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        // 2. Create User
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.DOCTOR);
        user.setActive(true);   // ✅ Doctor automatically active
        // 3. Save User
        User savedUser = userRepository.save(user);

        // 4. Create Doctor
        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setAge(request.getAge());
        doctor.setPhone(request.getPhone());
        doctor.setSalary(request.getSalary());
        doctor.setSpecialization(request.getSpecialization());

        // 5. Save Doctor
        Doctor savedDoctor = doctorRepository.save(doctor);

        // 6. Return Response
        return DoctorResponse.builder()
                .doctorId(savedDoctor.getId())
                .name(savedDoctor.getUser().getName())
                .email(savedDoctor.getUser().getEmail())
                .age(savedDoctor.getAge())
                .salary(savedDoctor.getSalary())
                .phone(savedDoctor.getPhone())
                .specialization(savedDoctor.getSpecialization())
                .build();
    }

	

    @Override
    public List<DoctorResponse> getAlldoctor() {

        List<Doctor> doctors = doctorRepository.findAll();
        List<DoctorResponse> responseList = new ArrayList<>();

        for (Doctor doctor : doctors) {

            DoctorResponse response = DoctorResponse.builder()
                    .doctorId(doctor.getId())
                    .name(doctor.getUser().getName())
                    .email(doctor.getUser().getEmail())
                    .age(doctor.getAge())
                    .salary(doctor.getSalary())
                    .phone(doctor.getPhone())
                    .specialization(doctor.getSpecialization())
                    .build();

            responseList.add(response);
        }

        return responseList;
    }



    @Override
    public DoctorResponse getDoctorById(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return DoctorResponse.builder()
                .doctorId(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .age(doctor.getAge())
                .phone(doctor.getPhone())
                .salary(doctor.getSalary())
                .specialization(doctor.getSpecialization())
                .build();
    }



   
	



	


	@Override
	public DoctorResponse updateDoctor(Long id, DoctorRegisterRequestDto request) {

	    List<Doctor> doctors = doctorRepository.findAll();

	    for (Doctor doctor : doctors) {

	        if (doctor.getId().equals(id)) {

	            User user = doctor.getUser();

	            user.setName(request.getName());
	            user.setEmail(request.getEmail());
	            user.setPassword(passwordEncoder.encode(request.getPassword()));

	            userRepository.save(user);

	            doctor.setAge(request.getAge());
	            doctor.setPhone(request.getPhone());
	            doctor.setSalary(request.getSalary());
	            doctor.setSpecialization(request.getSpecialization());

	            Doctor updatedDoctor = doctorRepository.save(doctor);

	            return DoctorResponse.builder()
	                    .doctorId(updatedDoctor.getId())
	                    .name(updatedDoctor.getUser().getName())
	                    .email(updatedDoctor.getUser().getEmail())
	                    .age(updatedDoctor.getAge())
	                    .phone(updatedDoctor.getPhone())
	                    .salary(updatedDoctor.getSalary())
	                    .specialization(updatedDoctor.getSpecialization())
	                    .build();
	        }
	    }

	    throw new RuntimeException("Doctor not found");
	}
	
	public void deleteDoctor(Long id) {

		Doctor doctor = doctorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Doctor not found"));

		// Doctor.user is mapped with cascade = ALL, so removing the doctor
		// also removes the linked login (User) record.
		doctorRepository.delete(doctor);
	}

	// ===================== Medical staff management =====================

	@Override
	public MedicalResponse registerMedical(MedicalRegisterRequestDto request) {

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.MEDICAL);

		User savedUser = userRepository.save(user);

		Medical medical = Medical.builder()
				.user(savedUser)
				.phone(request.getPhone())
				.gender(request.getGender())
				.department(request.getDepartment())
				.build();

		Medical savedMedical = medicalRepository.save(medical);

		return toMedicalResponse(savedMedical);
	}

	@Override
	public List<MedicalResponse> getAllMedical() {

		List<Medical> medicalStaff = medicalRepository.findAll();
		List<MedicalResponse> responseList = new ArrayList<>();

		for (Medical medical : medicalStaff) {
			responseList.add(toMedicalResponse(medical));
		}

		return responseList;
	}

	@Override
	public MedicalResponse getMedicalById(Long id) {

		Medical medical = medicalRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Medical staff not found"));

		return toMedicalResponse(medical);
	}

	@Override
	public MedicalResponse updateMedical(Long id, MedicalRegisterRequestDto request) {

		Medical medical = medicalRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Medical staff not found"));

		User user = medical.getUser();
		user.setName(request.getName());
		user.setEmail(request.getEmail());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		userRepository.save(user);

		medical.setPhone(request.getPhone());
		medical.setGender(request.getGender());
		medical.setDepartment(request.getDepartment());

		Medical updated = medicalRepository.save(medical);

		return toMedicalResponse(updated);
	}

	@Override
	public void deleteMedical(Long id) {

		Medical medical = medicalRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Medical staff not found"));

		medicalRepository.delete(medical);
	}

	// ===== FR1.3: generic staff account management (deactivate rather than delete) =====

	@Override
	public List<StaffAccountResponse> getAllStaffAccounts() {

		List<StaffAccountResponse> responseList = new ArrayList<>();

		for (User user : userRepository.findAll()) {

			// Staff accounts only - patients self-manage via their own profile.
			if (user.getRole() == Role.PATIENT) {
				continue;
			}

			responseList.add(toStaffAccountResponse(user));
		}

		return responseList;
	}

	@Override
	public StaffAccountResponse deactivateStaffAccount(Long userId) {
		return setStaffActiveState(userId, false);
	}

	@Override
	public StaffAccountResponse activateStaffAccount(Long userId) {
		return setStaffActiveState(userId, true);
	}

	private StaffAccountResponse setStaffActiveState(Long userId, boolean active) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Staff account not found"));

		if (user.getRole() == Role.PATIENT) {
			throw new RuntimeException("This endpoint manages staff accounts, not patient accounts");
		}

		user.setActive(active);
		User saved = userRepository.save(user);

		return toStaffAccountResponse(saved);
	}

	private StaffAccountResponse toStaffAccountResponse(User user) {

		return StaffAccountResponse.builder()
				.userId(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.role(user.getRole().name())
				.active(user.isActive())
				.build();
	}

	private MedicalResponse toMedicalResponse(Medical medical) {

		return MedicalResponse.builder()
				.id(medical.getId())
				.name(medical.getUser().getName())
				.email(medical.getUser().getEmail())
				.phone(medical.getPhone())
				.gender(medical.getGender())
				.department(medical.getDepartment())
				.role(medical.getUser().getRole().name())
				.build();
	}



	@Override
	public StaffAccountResponse createStaffAccount(StaffAccountRequest request) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public StaffAccountResponse updateStaffAccount(Long userId, StaffAccountRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}