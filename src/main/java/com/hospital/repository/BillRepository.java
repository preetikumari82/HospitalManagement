package com.hospital.repository; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository; import com.hospital.enums.PaymentStatus; import com.hospital.model.Bill; public interface BillRepository extends JpaRepository<Bill,Long>{ 
 @org.springframework.data.jpa.repository.Query("select coalesce(sum(b.paidAmount),0) from Bill b where b.createdAt >= :from and b.createdAt < :to")
 Double sumPaidBetween(@org.springframework.data.repository.query.Param("from") java.time.LocalDateTime from,
                       @org.springframework.data.repository.query.Param("to") java.time.LocalDateTime to); List<Bill> findByPatientIdOrderByCreatedAtDesc(Long id); List<Bill> findByStatusOrderByCreatedAtDesc(PaymentStatus status); }
