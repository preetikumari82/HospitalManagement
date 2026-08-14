package com.hospital.model;

import java.time.LocalDate;
import java.util.List;
import com.hospital.enums.*;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy="patient", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Medicine> medicines;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="doctor_id")
    private Doctor doctor;

    private int age;
    private String gender;
    private String phone;
    private String address;
    private long fees;
    private String disease;

    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    @Enumerated(EnumType.STRING)
    private EncounterType encounterType;

    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String bedNumber;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;

    @Column(length=2000)
    private String allergies;
    @Column(length=4000)
    private String pastConditions;
    @Column(length=4000)
    private String prescriptions;

    @Column(length=4000)
    private String treatment;
    @Column(length=2000)
    private String nurseRemarks;

    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="nurse_id")
    private Nurse nurse;
}
