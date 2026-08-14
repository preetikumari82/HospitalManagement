package com.hospital.Dto;
import com.hospital.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter; import lombok.Setter;
@Getter @Setter
public class StaffAccountRequest {
    @NotBlank private String name;
    @NotBlank @Email private String email;
    private String password;
    @NotNull private Role role;
}
