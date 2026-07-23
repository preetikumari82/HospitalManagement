package com.hospital.controller;

import com.hospital.repository.DocterRepository;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.hospital.imp.AdminServceImp;


@RestController
@RequestMapping("/api/admin")
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
	    @PostMapping("/ragisterdoctor")
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
	        return ResponseEntity.ok("Doctor deleted successfully."+servive.getDoctorById(id));
	    }

	    
	    
	    
	 

}
