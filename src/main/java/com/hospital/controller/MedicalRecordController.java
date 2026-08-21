package com.hospital.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import com.hospital.Dto.*; import com.hospital.service.MedicalRecordService;
@RestController @RequestMapping("/api/medical-records") public class MedicalRecordController{
 private final MedicalRecordService s; public MedicalRecordController(MedicalRecordService s){this.s=s;}
 @GetMapping("/{patientId}") public List<MedicalRecordResponse> byPatient(@PathVariable Long patientId){return s.byPatient(patientId);}
 @PostMapping @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')") public ResponseEntity<MedicalRecordResponse> create(@RequestBody MedicalRecordRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.create(r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')") public MedicalRecordResponse update(@PathVariable Long id,@RequestBody MedicalRecordRequest r){return s.update(id,r);}
 @PostMapping("/{id}/prescriptions") @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')") public PrescriptionResponse addPrescription(@PathVariable Long id,@RequestBody PrescriptionRequest r){return s.addPrescription(id,r);}
 @GetMapping("/{id}/prescriptions") public List<PrescriptionResponse> prescriptions(@PathVariable Long id){return s.prescriptions(id);}
}
