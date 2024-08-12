package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

}
