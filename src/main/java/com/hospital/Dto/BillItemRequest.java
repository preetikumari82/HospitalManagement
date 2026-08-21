package com.hospital.Dto;
import com.hospital.enums.BillItemType; import lombok.*; @Getter @Setter public class BillItemRequest { private BillItemType itemType; private String description; private Double amount; private Integer quantity=1; }
