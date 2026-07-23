package com.hospital.Dto;

import com.hospital.model.Medicine.MedicineBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MedicineItemResponse {
	  private String medicineName;
	    private String dosage;
	    private String timing;
	    

	    
		 
}
