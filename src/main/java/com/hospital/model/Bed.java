package com.hospital.model;

import com.hospital.enums.BedStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beds", uniqueConstraints = @UniqueConstraint(columnNames = {"ward_name", "bed_number"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bed {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ward_name", nullable = false, length = 100)
    private String wardName;

    @Column(name = "bed_number", nullable = false, length = 50)
    private String bedNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BedStatus status = BedStatus.AVAILABLE;
}
