package com.hospital.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineRateUpdateRequest {

    // Price per unit - required
    private Double rate;

    // Optional - if not sent, existing quantity is kept
    private Integer quantity;
}
