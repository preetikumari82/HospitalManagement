package com.hospital.controller;

import com.hospital.repository.DocterRepository;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.Dto.DoctorRegisterRequestDto;
import com.hospital.Dto.DoctorResponse;
import com.hospital.Dto.MedicalRegisterRequestDto;
import com.hospital.Dto.MedicalResponse;
import com.hospital.Dto.StaffAccountResponse;
import com.hospital.Dto.StaffAccountRequest;
import jakarta.validation.Valid;
import com.hospital.imp.AdminServceImp;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	 
//	initialige doctor repo
	private final DocterRepository docterRepository;
	
// initialige serveice
	@Autowired
	 private AdminServceImp servive;

	AdminController(DocterRepository docterRepository) {
		this.docterRepository = docterRepository;
	}
	  
	
//	==++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ragister
	 
	    @PostMapping("/registerdoctor")
	    public ResponseEntity<DoctorResponse> registerDoctor(
	    		
	            @RequestBody DoctorRegisterRequestDto request) {

	    	DoctorResponse response = servive.registerDoctor(request);

	        return (ResponseEntity<DoctorResponse>) ResponseEntity.ok(response);
	    }
	   

	    //=============================================================
	    // get all doctor
	   
	    @GetMapping("/getalldoctors")
	    public ResponseEntity<List<DoctorResponse>> getAllDoctor() {
	        return ResponseEntity.ok(servive.getAlldoctor());
	    }
	    
	   
	  
	    @PutMapping("/updatedoctor/{id}")
	    public ResponseEntity<DoctorResponse> updateDoctor(
	            @PathVariable Long id,
	            @RequestBody DoctorRegisterRequestDto request) {

	        return ResponseEntity.ok(servive.updateDoctor(id, request));
	    }
	    
	    
	    @DeleteMapping("/deletedoctors/{id}")
	    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {

	    	servive.deleteDoctor(id);
	        return ResponseEntity.ok("Doctor deleted successfully.");
	    }

	    //=============================================================
	    // Medical (pharmacy/billing) staff management

	    @PostMapping("/registerMedical")
	    public ResponseEntity<MedicalResponse> registerMedical(
	            @RequestBody MedicalRegisterRequestDto request) {

	        return ResponseEntity.ok(servive.registerMedical(request));
	    }

	    @GetMapping("/getAllMedical")
	    public ResponseEntity<List<MedicalResponse>> getAllMedical() {

	        return ResponseEntity.ok(servive.getAllMedical());
	    }

	    @GetMapping("/getMedicalById/{id}")
	    public ResponseEntity<MedicalResponse> getMedicalById(@PathVariable Long id) {

	        return ResponseEntity.ok(servive.getMedicalById(id));
	    }

	    @PutMapping("/updateMedical/{id}")
	    public ResponseEntity<MedicalResponse> updateMedical(
	            @PathVariable Long id,
	            @RequestBody MedicalRegisterRequestDto request) {

	        return ResponseEntity.ok(servive.updateMedical(id, request));
	    }

	    @DeleteMapping("/deleteMedical/{id}")
	    public ResponseEntity<String> deleteMedical(@PathVariable Long id) {

	        servive.deleteMedical(id);
	        return ResponseEntity.ok("Medical staff deleted successfully.");
	    }

	    //=============================================================
	    // FR1.3: generic staff account management (list / deactivate / activate)

	    @GetMapping("/staff")
	    public ResponseEntity<List<StaffAccountResponse>> getAllStaff() {

	        return ResponseEntity.ok(servive.getAllStaffAccounts());
	    }

	    @PostMapping("/staff")
    public ResponseEntity<StaffAccountResponse> createStaff(@Valid @RequestBody StaffAccountRequest request) {
        return ResponseEntity.ok(servive.createStaffAccount(request));
    }

    @PutMapping("/staff/{userId}")
    public ResponseEntity<StaffAccountResponse> updateStaff(@PathVariable Long userId,
                                                             @Valid @RequestBody StaffAccountRequest request) {
        return ResponseEntity.ok(servive.updateStaffAccount(userId, request));
    }

    @PatchMapping("/staff/{userId}/deactivate")
	    public ResponseEntity<StaffAccountResponse> deactivateStaff(@PathVariable Long userId) {

	        return ResponseEntity.ok(servive.deactivateStaffAccount(userId));
	    }

	    @PatchMapping("/staff/{userId}/activate")
	    public ResponseEntity<StaffAccountResponse> activateStaff(@PathVariable Long userId) {

	        return ResponseEntity.ok(servive.activateStaffAccount(userId));
	    }

}
