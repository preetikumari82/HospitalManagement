package com.hospital.model;
import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="prescriptions")
public class Prescription {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="medical_record_id") private MedicalRecord medicalRecord;
 @Column(nullable=false) private String medicineName;
 private String dosage; private String duration; private String instructions;
}
