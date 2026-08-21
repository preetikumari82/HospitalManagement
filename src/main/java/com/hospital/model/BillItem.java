package com.hospital.model;
import com.hospital.enums.BillItemType; import jakarta.persistence.*; import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="bill_items")
public class BillItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="bill_id") private Bill bill;
 @Enumerated(EnumType.STRING) @Column(name="item_type",nullable=false) private BillItemType itemType;
 @Column(nullable=false) private String description;
 @Column(nullable=false) private Double amount;
 @Column(nullable=false) private Integer quantity=1;
}
