package com.hospital.model;

import java.time.LocalDate;
import java.util.List;

import com.hospital.Dto.MedicineItemRequest;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

    private String medicineName;
    private String dosage;
    private String timing;

    @ManyToOne
    private Patient patient;

	public Object getPrescribedDate() {
		// TODO Auto-generated method stub
		return null;
	}
}

    
