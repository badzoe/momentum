package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.services.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/withdrawals")
@Tag(name="Withdrawal")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping("/{productId}/{amount}")
    @Operation(summary = "Withdraw Product")
    public ResponseEntity<String> createWithdrawal(@PathVariable String productId, @PathVariable String amount) {
        return withdrawalService.processWithdrawal(Long.valueOf(productId),BigDecimal.valueOf(Long.parseLong(amount)));
    }
}