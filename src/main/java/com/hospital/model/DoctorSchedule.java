package com.hospital.model;
import java.time.DayOfWeek;
import java.time.LocalTime;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="doctor_schedules", uniqueConstraints=@UniqueConstraint(columnNames={"doctor_id","day_of_week","start_time"}))
public class DoctorSchedule {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="doctor_id") private Doctor doctor;
 @Enumerated(EnumType.STRING) @Column(name="day_of_week", nullable=false) private DayOfWeek dayOfWeek;
 @Column(name="start_time", nullable=false) private LocalTime startTime;
 @Column(name="end_time", nullable=false) private LocalTime endTime;
 @Column(nullable=false) @Builder.Default private Integer slotMinutes=30;
 @Column(nullable=false) @Builder.Default private boolean active=true;
}
