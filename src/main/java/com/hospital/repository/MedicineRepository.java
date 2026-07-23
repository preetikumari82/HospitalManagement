package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.model.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByPatientId(Long patientId);

}