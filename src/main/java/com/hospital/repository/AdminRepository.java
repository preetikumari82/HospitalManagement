package com.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hospital.model.Admin;

public interface AdminRepository
        extends JpaRepository<Admin,Integer> {

	 boolean existsByUserEmail(String email);
}