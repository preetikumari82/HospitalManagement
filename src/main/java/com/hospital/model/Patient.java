package com.hospital.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.hospital.enums.PatientStatus;
import com.hospital.enums.Specialization;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
///// one patient have many madecine
    
    @OneToMany(mappedBy = "patient",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
 private List<Medicine> medicines;
    
    // Patient ka login/user data
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    // one doctor have many patient 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    private int age;

    private String gender;

    private String phone;

    private String address;

    private long fees;

    // disease of patient
    private String disease;
    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;
 // Treatment Details
    private String treatment;

  

    // Nurse Remarks
    private String nurseRemarks;

    // Disease ke hisaab se doctor type
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id")
    private Nurse nurse;

	public void setAdmitDateTime(LocalDateTime now) {
		// TODO Auto-generated method stub
		
	}

	public LocalDateTime getAdmitDateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public Patient getNurse() {
		// TODO Auto-generated method stub
		return null;
	}
} 