package com.hospital.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaffAccountResponse {

    private Long userId;
    private String name;
    private String email;
    private String role;
    private boolean active;
}
