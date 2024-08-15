package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawalsResponse(Long id, String product, String Status, BigDecimal amount, LocalDateTime transactionTime, String Investor) {
}
