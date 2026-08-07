package com.hospital.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineItemResponse {

    private Long id;
    private String medicineName;
    private String dosage;
    private String timing;

    private Integer quantity;

    // Price per unit
    private Double rate;

    // Auto calculated = rate * quantity
    private Double total;
}
