package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request;

import java.math.BigDecimal;

public record InvestProduct( String type,String name, BigDecimal balance,Long investor) {
}