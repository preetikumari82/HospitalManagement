package com.hospital.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicine_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String medicineName;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Double rate;
}