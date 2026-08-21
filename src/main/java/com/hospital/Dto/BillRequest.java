package com.hospital.Dto;
import java.util.*; import lombok.*; @Getter @Setter public class BillRequest { private Long patientId; private Long appointmentId; private List<BillItemRequest> items=new ArrayList<>(); }
