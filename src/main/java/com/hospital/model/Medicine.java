package com.hospital.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicineName;
    private String dosage;
    private String timing;

    // How many units of this medicine were given
    @Builder.Default
    private Integer quantity = 1;

    // Price per unit, set/updated by Medical staff
    @Builder.Default
    private Double rate = 0.0;

    // Auto calculated = rate * quantity, always kept in sync (see calculateTotal below)
    @Builder.Default
    private Double total = 0.0;

    private LocalDate prescribedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Keeps `total` and `prescribedDate` always correct and in sync with rate/quantity,
    // no matter which controller/service saves or updates the row (Nurse or Medical).
    @PrePersist
    @PreUpdate
    private void calculateTotal() {
        if (this.rate == null) {
            this.rate = 0.0;
        }
        if (this.quantity == null || this.quantity <= 0) {
            this.quantity = 1;
        }
        this.total = this.rate * this.quantity;

        if (this.prescribedDate == null) {
            this.prescribedDate = LocalDate.now();
        }
    }
}
