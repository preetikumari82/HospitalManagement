package com.hospital.model;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="medical_records")
public class MedicalRecord {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="patient_id") private Patient patient;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="doctor_id") private Doctor doctor;
 @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="appointment_id", unique=true) private Appointment appointment;
 @Column(length=2000) private String diagnosis;
 @Column(length=5000) private String notes;
 @Column(nullable=false) private LocalDateTime createdAt;
 @OneToMany(mappedBy="medicalRecord",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<Prescription> prescriptions=new ArrayList<>();
 @PrePersist void prePersist(){if(createdAt==null)createdAt=LocalDateTime.now();}
}
