package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses;

import java.math.BigDecimal;
public record InvestorProductsResponse(long id,
                                       String type,
                                       String name,
                                       BigDecimal balance) {
}