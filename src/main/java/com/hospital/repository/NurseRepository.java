package com.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.model.Nurse;

@Repository
public interface NurseRepository extends JpaRepository<Nurse, Long> {

	Optional<Nurse> findByUserId(Long userId);

}