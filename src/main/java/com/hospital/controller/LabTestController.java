package com.hospital.controller;
import java.util.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import com.hospital.Dto.*; import com.hospital.enums.LabTestStatus; import com.hospital.service.LabTestService;
@RestController @RequestMapping("/api/lab-tests") public class LabTestController{
 private final LabTestService s; public LabTestController(LabTestService s){this.s=s;}
 @GetMapping public List<LabTestResponse> all(){return s.all();} @GetMapping("/patient/{patientId}") public List<LabTestResponse> patient(@PathVariable Long patientId){return s.byPatient(patientId);}
 @PostMapping @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')") public LabTestResponse create(@RequestBody LabTestRequest r){return s.create(r);}
 @PutMapping("/{id}/result") @PreAuthorize("hasAnyRole('LAB_TECH','ADMIN')") public LabTestResponse result(@PathVariable Long id,@RequestBody LabTestResultRequest r){return s.result(id,r);}
 @PutMapping("/{id}/status") @PreAuthorize("hasAnyRole('LAB_TECH','ADMIN')") public LabTestResponse status(@PathVariable Long id,@RequestParam LabTestStatus status){return s.status(id,status);}
}
