package com.hospital.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hospital.Dto.NurseRequest;
import com.hospital.Dto.NurseResponse;
import com.hospital.Dto.PatientRequest;
import com.hospital.Dto.PatientResponse;
import com.hospital.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctor")
@PreAuthorize("hasRole('DOCTOR')") // FR1.2
public class DoctorController {

    private final DoctorService patientService;

    public DoctorController(DoctorService patientService) {
        this.patientService = patientService;
    }
////////////////patient Apis////////////////
    // Admit Patient
    @PostMapping("/admitPatient")
    public ResponseEntity<PatientResponse> admitPatient( 
            @RequestBody PatientRequest request) {

        PatientResponse response = (PatientResponse) patientService.admitPatient(request);
        return ResponseEntity.ok(response);
    }
    ////////get all patients///
  
   
        
        @GetMapping("/allpatients")
        public ResponseEntity<List<PatientResponse>> getAllPatients(){
            return ResponseEntity.ok(patientService.getAllPatients());
        
    }
        ///// get patient by id//////getBypatientid
        @GetMapping("/getBypatientid/{id}")
        public ResponseEntity<PatientResponse> getPatientById(
                @PathVariable Long id) {

            return ResponseEntity.ok(patientService.getPatientById(id));
        }
        /////// update by id/////
        
  
        @PutMapping("/updatePatientByid/{id}")
        public ResponseEntity<PatientResponse> updatePatient(
                @PathVariable Long id,
                @RequestBody PatientRequest request) {

            return ResponseEntity.ok(patientService.updatePatient(id, request));
        }
        /////////// patient discharge by id/////
       

            @PutMapping("/patientdischargeByid/{id}")
            public ResponseEntity<PatientResponse> dischargePatient(@PathVariable Long id) {

                return ResponseEntity.ok(patientService.dischargePatient(id));
            
        }
        
    ///////////////CREATE   NURSE//////////

            // Doctor creates Nurse
            @PostMapping("/createNurse")
            public ResponseEntity<NurseResponse> createNurse(
                    @RequestBody NurseRequest request){

                return ResponseEntity.ok(patientService.createNurse(request));
            }
            
            // DOctor get allnurse //
            @GetMapping("/getAllNurses")
            public ResponseEntity<List<NurseResponse>> getAllNurses() {

                return ResponseEntity.ok(patientService.getAllNurses());

            }
            @GetMapping("/getNurseById/{id}")
            public ResponseEntity<NurseResponse> getNurseById(
                    @PathVariable Long id) {

                return ResponseEntity.ok(patientService.getNurseById(id));
            }
            @PutMapping("/updateNurse/{id}")
            public ResponseEntity<NurseResponse> updateNurse(
                    @PathVariable Long id,
                    @RequestBody NurseRequest request) {

                return ResponseEntity.ok(
                        patientService.updateNurse(id, request));
            }
            @DeleteMapping("/deleteNurse/{id}")
            public ResponseEntity<String> deleteNurse(
                    @PathVariable Long id) {

                return ResponseEntity.ok(
                        patientService.deleteNurse(id));
            }
            
            

}