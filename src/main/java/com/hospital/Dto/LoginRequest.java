package com.hospital.Dto;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class LoginRequest {
    private String email;
    private String username;
    private String password;
    public String getIdentifier() {
        if (email != null && !email.isBlank()) return email.trim();
        return username == null ? "" : username.trim();
    }
}
