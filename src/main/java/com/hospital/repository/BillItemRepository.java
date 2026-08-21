package com.hospital.repository;
import com.hospital.model.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BillItemRepository extends JpaRepository<BillItem,Long> { }
