package com.hospital.controller;

import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.Dto.ForgotPasswordRequest;
import com.hospital.Dto.ForgotPasswordResponse;
import com.hospital.Dto.LoginRequest;
import com.hospital.Dto.LoginResponse;
import com.hospital.Dto.ResetPasswordRequest;
import com.hospital.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse  response) {

        LoginResponse loginresponse = authService.login(request);
// get jwt token from loginresponse and set in the response header 
        String token =loginresponse.getToken();
        System.out.println("JWT TOKEN = " + token);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build();

        System.out.println("COOKIE = " + cookie);

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok(loginresponse);
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok("Logout successful");
    }

    // FR1.5: Step 1 - request an OTP be emailed to the account's address
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        ForgotPasswordResponse response =
                authService.forgotPassword(request);

        return ResponseEntity.ok(response);
    }

    // FR1.5: Step 2 - verify OTP and set a new password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok("Password has been reset successfully.");
    }
}