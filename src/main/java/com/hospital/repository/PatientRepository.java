package com.hospital.repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hospital.model.Patient;
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserId(Long userId);
    List<Patient> findByUser_NameContainingIgnoreCase(String name);
    List<Patient> findByPhoneContaining(String phone);
    List<Patient> findByStatus(com.hospital.enums.PatientStatus status);
}
