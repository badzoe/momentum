package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String type;
    private String name;
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    private Investor investor;

}
