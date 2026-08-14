package com.hospital.Dto;

import java.time.LocalDateTime;

public class ForgotPasswordResponse {

    private String message;
    private String email;
    private String otp;
    private LocalDateTime expiryTime;
    private long expiryMinutes;

    public ForgotPasswordResponse(
            String message,
            String email,
            String otp,
            LocalDateTime expiryTime,
            long expiryMinutes) {

        this.message = message;
        this.email = email;
        this.otp = otp;
        this.expiryTime = expiryTime;
        this.expiryMinutes = expiryMinutes;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public long getExpiryMinutes() {
        return expiryMinutes;
    }
}