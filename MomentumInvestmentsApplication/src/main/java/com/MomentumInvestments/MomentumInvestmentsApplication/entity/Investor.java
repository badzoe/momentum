package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Investor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private String role;

    @OneToMany(mappedBy = "investor")
    private List<InvestorProducts> investorProducts;
}