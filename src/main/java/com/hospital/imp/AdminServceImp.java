package com.hospital.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.Dto.DoctorRegisterRequestDto;
import com.hospital.Dto.DoctorResponse;
import com.hospital.enums.Role;
import com.hospital.model.Doctor;
import com.hospital.model.User;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.UserRepository;
import com.hospital.service.AdminService;

@Service
public class AdminServceImp implements AdminService {

    @Autowired
    private DocterRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    

    @Override
    public DoctorResponse registerDoctor(
            DoctorRegisterRequestDto request) {

        // 1. User create
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(Role.DOCTOR);

        // 2. User save
        User savedUser = userRepository.save(user);

        // 3. Doctor create
        Doctor doctor = new Doctor();

        doctor.setUser(savedUser);
        doctor.setAge(request.getAge());
        doctor.setPhone(request.getPhone());
        doctor.setSalary(request.getSalary());
        doctor.setSpecialization(request.getSpecialization());

        // 4. Doctor save
        Doctor savedDoctor = doctorRepository.save(doctor);
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
		// TODO Auto-generated method stub
		
	}
}