package com.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.model.Doctor;

public interface DocterRepository extends JpaRepository<Doctor,Long>{
	


}
