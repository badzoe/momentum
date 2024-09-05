package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;


@Entity
@Setter
@Getter
@ToString
public class InvestorProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productID;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    private Investor investorID;

    private BigDecimal balance;

    public void setType(ProductType productType) {
    }
}