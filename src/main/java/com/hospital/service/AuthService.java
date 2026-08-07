package com.hospital.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.Dto.LoginRequest;
import com.hospital.Dto.LoginResponse;
import com.hospital.config.JwtService;
import com.hospital.model.User;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.MedicalRepository;
import com.hospital.repository.NurseRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DocterRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final MedicalRepository medicalRepository;
    private final PatientRepository patientRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       DocterRepository doctorRepository,
                       NurseRepository nurseRepository,
                       MedicalRepository medicalRepository,
                       PatientRepository patientRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.medicalRepository = medicalRepository;
        this.patientRepository = patientRepository;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        Long profileId = resolveProfileId(user);

        return new LoginResponse(
                "Login successful",
                user.getName(),
                user.getEmail(),
                token,
                user.getRole(),
                profileId
        );
    }

    // Looks up the role-specific record for this user so the frontend
    // (e.g. a Doctor creating a Nurse) doesn't have to ask the user for it.
    private Long resolveProfileId(User user) {

        switch (user.getRole()) {
            case DOCTOR:
                return doctorRepository.findByUserId(user.getId())
                        .map(d -> d.getId())
                        .orElse(null);
            case NURSE:
                return nurseRepository.findByUserId(user.getId())
                        .map(n -> n.getId())
                        .orElse(null);
            case MEDICAL:
                return medicalRepository.findByUserId(user.getId())
                        .map(m -> m.getId())
                        .orElse(null);
            case PATIENT:
                return patientRepository.findByUserId(user.getId())
                        .map(p -> p.getId())
                        .orElse(null);
            default:
                return null;
        }
    }

}