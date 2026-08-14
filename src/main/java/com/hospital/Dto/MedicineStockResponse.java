package com.hospital.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineStockResponse {

    private Long id;

    private String medicineName;

    private Integer availableQuantity;

    private Double rate;

    private Boolean available;
}