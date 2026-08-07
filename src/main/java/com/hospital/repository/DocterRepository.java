package com.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.model.Doctor;

public interface DocterRepository extends JpaRepository<Doctor,Long>{

	Optional<Doctor> findByUserId(Long userId);

}
