package com.hospital.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medical_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Medical staff login/user data
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String phone;

    private String gender;

    // e.g. Pharmacy, Billing, Store
    private String department;
}
