package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses;

import java.math.BigDecimal;
public record InvestorProducts(long id,
                               String type,
                               String name,
                               BigDecimal balance) {
}