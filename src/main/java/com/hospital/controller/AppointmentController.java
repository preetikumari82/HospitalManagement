package com.hospital.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import com.hospital.Dto.*; import com.hospital.enums.AppointmentStatus; import com.hospital.service.AppointmentService;
@RestController @RequestMapping("/api/appointments") public class AppointmentController{
 private final AppointmentService s; public AppointmentController(AppointmentService s){this.s=s;}
 @GetMapping public List<AppointmentResponse> all(){return s.all();}
 @GetMapping("/{id}") public AppointmentResponse get(@PathVariable Long id){return s.get(id);}
 @GetMapping("/doctor/{doctorId}") public List<AppointmentResponse> doctor(@PathVariable Long doctorId){return s.byDoctor(doctorId);}
 @GetMapping("/patient/{patientId}") public List<AppointmentResponse> patient(@PathVariable Long patientId){return s.byPatient(patientId);}
 @PostMapping @PreAuthorize("hasAnyRole('PATIENT','RECEPTIONIST','ADMIN')") public ResponseEntity<AppointmentResponse> create(@RequestBody AppointmentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(s.create(r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('PATIENT','RECEPTIONIST','ADMIN','DOCTOR')") public AppointmentResponse update(@PathVariable Long id,@RequestBody AppointmentRequest r){return s.update(id,r);}
 @PutMapping("/{id}/status") @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN','DOCTOR')") public AppointmentResponse status(@PathVariable Long id,@RequestParam AppointmentStatus status){return s.status(id,status);}
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('PATIENT','RECEPTIONIST','ADMIN')") public void cancel(@PathVariable Long id){s.cancel(id);}
}
