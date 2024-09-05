package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record WithdrawalsResponse(
        Long id,
        String product,
        String status,
        BigDecimal amount,
        LocalDateTime transactionTime,
        String investorName
) {
    // The record automatically generates getters for all fields, so explicit getter methods are not needed

    // If you need custom methods, you can add them here
    // For example:
    public String formattedTransactionTime() {
        return transactionTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
