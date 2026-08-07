package com.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.model.Medical;

public interface MedicalRepository extends JpaRepository<Medical, Long> {

	Optional<Medical> findByUserId(Long userId);

}
