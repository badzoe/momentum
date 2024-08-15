package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request;

import java.math.BigDecimal;

public record InvestProductRequest( Long investorID,Long  productID, BigDecimal balance) {
}