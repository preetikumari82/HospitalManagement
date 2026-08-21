package com.hospital.model;
import java.time.*;
import com.hospital.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="appointments", indexes={@Index(columnList="doctor_id,appointment_date,time_slot"),@Index(columnList="patient_id,appointment_date")})
public class Appointment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="patient_id") private Patient patient;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="doctor_id") private Doctor doctor;
 @Column(nullable=false) private LocalDate appointmentDate;
 @Column(name="time_slot",nullable=false) private LocalTime timeSlot;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private AppointmentStatus status=AppointmentStatus.PENDING;
 @Column(length=1000) private String reason;
 @Column(nullable=false) private boolean reminderSent=false;
 @Column(nullable=false) private LocalDateTime createdAt;
 @PrePersist void prePersist(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
