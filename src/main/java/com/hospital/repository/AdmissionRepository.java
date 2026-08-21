package com.hospital.repository;

import com.hospital.enums.AdmissionStatus;
import com.hospital.model.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    List<Admission> findByStatus(AdmissionStatus status);
    List<Admission> findByPatientIdOrderByAdmittedAtDesc(Long patientId);
    Optional<Admission> findFirstByPatientIdAndStatus(Long patientId, AdmissionStatus status);
}
