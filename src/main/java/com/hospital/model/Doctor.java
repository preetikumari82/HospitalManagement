package com.hospital.model;

import java.util.ArrayList;
import java.util.List;

import com.hospital.enums.Specialization;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Doctor ka login/user data
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private int age;

    private long salary;

    private String phone;

    // Doctor kis field ka specialist hai
    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    // ============================
    // NEW - Doctor Profile
    // ============================

    // Doctor ki qualification
    private String qualification;

    // Consultation fee
    private double consultationFee;

    // ============================
    // Doctor - Department
    // ============================

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // ============================
    // Doctor - Patients
    // ============================

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Patient> patients;

    // ============================
    // Doctor - Schedule
    // ============================
//
//    @OneToMany(
//            mappedBy = "doctor",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
////    private List<DoctorSchedule> schedules = new ArrayList<>();
//
//    // ============================
//    // Doctor - Leave
//    // ============================
//
//    @OneToMany(
//            mappedBy = "doctor",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
 // ============================
 // Doctor - Leave
 // ============================

 @OneToMany(
         mappedBy = "doctor",
         cascade = CascadeType.ALL,
         orphanRemoval = true
 )
 private List<DoctorLeave> leaves = new ArrayList<>();
  
}