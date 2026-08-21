package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDate;
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

    @Column(length = 100)
    private String category;

    @Column(length = 100)
    private String batchNumber;

    private LocalDate expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private Integer reorderLevel = 10;
}