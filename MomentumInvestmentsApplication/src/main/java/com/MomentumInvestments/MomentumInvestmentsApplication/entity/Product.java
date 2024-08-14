package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private ProductType type;
    private String name;
    private BigDecimal balance;
    @ManyToOne
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "investor_id")
    private Investor investor;

}