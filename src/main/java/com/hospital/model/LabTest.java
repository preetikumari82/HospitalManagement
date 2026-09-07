package com.hospital.model;
import java.time.*; import com.hospital.enums.LabTestStatus; import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="lab_tests")
public class LabTest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="patient_id") private Patient patient;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="doctor_id") private Doctor doctor;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medical_record_id") private MedicalRecord medicalRecord;
 @Column(nullable=false) private String testName;
 @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private LabTestStatus status=LabTestStatus.REQUESTED;
 @Column(length=2000) private String resultValue;
 private String resultFileUrl; private LocalDateTime requestedAt; private LocalDateTime completedAt;
 @PrePersist void pre(){if(requestedAt==null)requestedAt=LocalDateTime.now();}
}
