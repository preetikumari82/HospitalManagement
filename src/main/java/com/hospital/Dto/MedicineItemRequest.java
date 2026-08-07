package com.hospital.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineItemRequest {

    private String medicineName;
    private String dosage;
    private String timing;

    // Optional - defaults to 1 if not sent
    private Integer quantity;

    // Price per unit of this medicine
    private Double rate;
}
