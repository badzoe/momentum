package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private ProductType type;
    private String name;

    @OneToMany(mappedBy = "product")
    private List<InvestorProducts> productInvestors;

}