package com.hospital.model;
import java.time.LocalDateTime; import java.util.*; import com.hospital.enums.PaymentStatus; import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="bills")
public class Bill {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="patient_id") private Patient patient;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="appointment_id") private Appointment appointment;
 @Column(nullable=false) @Builder.Default private Double totalAmount=0.0;
 @Column(nullable=false) @Builder.Default private Double paidAmount=0.0;
 @Enumerated(EnumType.STRING) @Column(nullable=false) @Builder.Default private PaymentStatus status=PaymentStatus.PENDING;
 @Column(nullable=false) private LocalDateTime createdAt;
 @OneToMany(mappedBy="bill",cascade=CascadeType.ALL,orphanRemoval=true) @Builder.Default private List<BillItem> items=new ArrayList<>();
 @PrePersist void pre(){if(createdAt==null)createdAt=LocalDateTime.now();}
}
