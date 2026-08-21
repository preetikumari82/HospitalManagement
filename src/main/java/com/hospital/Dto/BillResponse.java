package com.hospital.Dto;
import java.time.*; import java.util.*; import com.hospital.enums.PaymentStatus; import lombok.*;
@Getter @Setter @Builder public class BillResponse { private Long id,patientId,appointmentId; private String patientName; private Double totalAmount,paidAmount,balanceAmount; private PaymentStatus status; private LocalDateTime createdAt; private List<BillItemResponse> items; }
