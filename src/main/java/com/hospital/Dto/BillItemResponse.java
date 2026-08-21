package com.hospital.Dto;
import com.hospital.enums.BillItemType; import lombok.*; @Getter @Setter @Builder public class BillItemResponse { private Long id; private BillItemType itemType; private String description; private Double amount; private Integer quantity; }
