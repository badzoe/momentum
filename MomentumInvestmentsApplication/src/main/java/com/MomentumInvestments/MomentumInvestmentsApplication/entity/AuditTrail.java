package com.MomentumInvestments.MomentumInvestmentsApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "previous_balance")
    private BigDecimal previousBalance;

    @Column(name = "new_balance")
    private BigDecimal newBalance;

    @Column(name = "withdrawal_id")
    private Long withdrawalId;

    @Column(name = "previous_status")
    private String previousStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}