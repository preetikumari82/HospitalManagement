package com.hospital.controller;
import java.util.*; import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import com.hospital.Dto.*; import com.hospital.service.DoctorScheduleService;
@RestController @RequestMapping("/api/doctors") public class DoctorScheduleController{
 private final DoctorScheduleService service; public DoctorScheduleController(DoctorScheduleService s){service=s;}
 @GetMapping("/{doctorId}/schedule") public List<ScheduleResponse> get(@PathVariable Long doctorId){return service.getByDoctor(doctorId);}
 @PostMapping("/schedule") @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')") public ResponseEntity<ScheduleResponse> create(@RequestBody ScheduleRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));}
 @PutMapping("/schedule/{id}") @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')") public ScheduleResponse update(@PathVariable Long id,@RequestBody ScheduleRequest r){return service.update(id,r);}
 @DeleteMapping("/schedule/{id}") @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')") public void delete(@PathVariable Long id){service.delete(id);}
}
