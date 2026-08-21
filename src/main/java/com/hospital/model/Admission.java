package com.hospital.model;

import com.hospital.enums.AdmissionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admissions", indexes = {
        @Index(columnList = "patient_id"),
        @Index(columnList = "bed_id"),
        @Index(columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Admission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bed_id")
    private Bed bed;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime admittedAt = LocalDateTime.now();

    private LocalDateTime dischargedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdmissionStatus status = AdmissionStatus.ACTIVE;
}
