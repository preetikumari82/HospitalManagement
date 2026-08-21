package com.hospital.repository;

import com.hospital.model.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DoctorLeaveRepository
        extends JpaRepository<DoctorLeave, Long> {

    List<DoctorLeave> findByDoctorId(Long doctorId);

    boolean existsByDoctorIdAndLeaveDate(
            Long doctorId,
            LocalDate leaveDate
    );
}