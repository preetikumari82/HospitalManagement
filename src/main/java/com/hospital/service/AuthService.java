package com.hospital.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.Dto.ForgotPasswordRequest;
import com.hospital.Dto.ForgotPasswordResponse;
import com.hospital.Dto.LoginRequest;
import com.hospital.Dto.LoginResponse;
import com.hospital.Dto.ResetPasswordRequest;
import com.hospital.config.EmailService;
import com.hospital.config.JwtService;
import com.hospital.model.PasswordResetOtp;
import com.hospital.model.User;
import com.hospital.repository.DocterRepository;
import com.hospital.repository.MedicalRepository;
import com.hospital.repository.NurseRepository;
import com.hospital.repository.PasswordResetOtpRepository;
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
    private final PasswordResetOtpRepository otpRepository;
    private final EmailService emailService;

    @Value("${otp.expiry-minutes:10}")
    private long otpExpiryMinutes;

    private static final SecureRandom RANDOM = new SecureRandom();

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       DocterRepository doctorRepository,
                       NurseRepository nurseRepository,
                       MedicalRepository medicalRepository,
                       PatientRepository patientRepository,
                       PasswordResetOtpRepository otpRepository,
                       EmailService emailService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.medicalRepository = medicalRepository;
        this.patientRepository = patientRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    public LoginResponse login(LoginRequest request) {

        String identifier = request.getIdentifier();
        User user = userRepository.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("Invalid username/email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // FR1.3: deactivated accounts must not be able to log in.
        if (!user.isActive()) {
            throw new RuntimeException("This account has been deactivated. Please contact the administrator.");
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

    // FR1.5: request an OTP be emailed to the account holder.
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("No account found with this email"));

        String otp = generateOtp();

        LocalDateTime expiryTime =
                LocalDateTime.now().plusMinutes(otpExpiryMinutes);

        PasswordResetOtp resetOtp = PasswordResetOtp.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiryTime(expiryTime)
                .used(false)
                .build();

        otpRepository.save(resetOtp);

        emailService.sendPlainText(
                user.getEmail(),
                "Hospital System - Password Reset OTP",
                "Your OTP for password reset is: " + otp +
                "\nThis OTP is valid for " + otpExpiryMinutes + " minutes." +
                "\nExpiry Time: " + expiryTime +
                "\nIf you did not request this, please ignore this email."
        );

        return new ForgotPasswordResponse(
                "OTP has been sent to your email.",
                user.getEmail(),
                otp,
                expiryTime,
                otpExpiryMinutes
        );
    }

    // FR1.5: verify the OTP and set the new password.
    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetOtp resetOtp = otpRepository
                .findTopByEmailAndUsedFalseOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No OTP request found for this email"));

        if (resetOtp.isUsed()) {
            throw new RuntimeException("This OTP has already been used");
        }

        if (resetOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        if (!resetOtp.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetOtp.setUsed(true);
        otpRepository.save(resetOtp);
    }

    private String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}