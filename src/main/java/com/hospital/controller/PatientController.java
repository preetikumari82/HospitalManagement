package com.hospital.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.hospital.Dto.*;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;
    public PatientController(PatientService service){this.service=service;}

    @PostMapping("/register")
    public ResponseEntity<PatientResponse> register(@Valid @RequestBody PatientRegisterRequest r)
    {return ResponseEntity.ok(service.selfRegister(r));}

    @GetMapping
    public ResponseEntity<List<PatientResponse>> all(@RequestParam(required=false) String q,
                                                      @RequestParam(required=false) String status){
        return ResponseEntity.ok(service.getAll(q,status));
    }

    @GetMapping("/me")
    public ResponseEntity<PatientResponse> me(Authentication authentication){
        return ResponseEntity.ok(service.getByEmail(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> byId(@PathVariable Long id){return ResponseEntity.ok(service.getById(id));}

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable Long id,@Valid @RequestBody PatientRequest r){return ResponseEntity.ok(service.update(id,r));}

    @PostMapping("/{id}/admit")
    public ResponseEntity<PatientResponse> admit(@PathVariable Long id,@RequestBody PatientRequest r){return ResponseEntity.ok(service.admit(id,r));}

    @PostMapping("/{id}/discharge")
    public ResponseEntity<PatientResponse> discharge(@PathVariable Long id,@RequestBody(required=false) PatientRequest r){
        return ResponseEntity.ok(service.discharge(id));
    }
}
