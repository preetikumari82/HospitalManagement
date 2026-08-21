package com.hospital.repository;

import com.hospital.enums.BedStatus;
import com.hospital.model.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByStatus(BedStatus status);
    boolean existsByWardNameAndBedNumber(String wardName, String bedNumber);
}
