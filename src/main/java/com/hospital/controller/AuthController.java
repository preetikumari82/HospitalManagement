package com.hospital.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.Dto.LoginRequest;
import com.hospital.Dto.LoginResponse;
import com.hospital.service.AuthService;

import jakarta.servlet.http.HttpSession;

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
            HttpSession session) {

        LoginResponse response = authService.login(request);

        // Login user ko session me save
        session.setAttribute("loginUser", response);

        return ResponseEntity.ok(response);
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {

        // Session destroy
        session.invalidate();

        return ResponseEntity.ok("Logout successful");
    }
}