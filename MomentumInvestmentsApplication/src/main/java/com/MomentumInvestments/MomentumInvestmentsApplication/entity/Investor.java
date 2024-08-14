package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
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

}
