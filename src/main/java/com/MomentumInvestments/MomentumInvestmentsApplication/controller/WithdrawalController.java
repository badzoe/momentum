package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.WithdrawalsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/withdrawals")
@Tag(name="Withdrawal")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping("/{userId}/{productId}/{amount}")
    @Operation(summary = "Withdraw Product")
    public ResponseEntity<String> createWithdrawal(@PathVariable String userId,@PathVariable String productId, @PathVariable String amount) throws ExecutionException, InterruptedException, TimeoutException {
        return withdrawalService.processWithdrawal(Long.valueOf(userId),Long.valueOf(productId),BigDecimal.valueOf(Long.parseLong(amount)));
    }

    @GetMapping("/successful/{productType}")
    @Operation(summary = "Successful Withdrawal Per Product Type")
    public ResponseEntity<List<WithdrawalsResponse>> successfulWithdrawalsPerProduct(@PathVariable String productType) {
        return withdrawalService.successfulWithdrawalsPerProduct(productType);
    }

    @GetMapping("/failed/{productType}")
    @Operation(summary = "Failed Withdrawal Per Product")
    public ResponseEntity<List<WithdrawalsResponse>> failedWithdrawalsPerProduct(@PathVariable String productType) {
        return withdrawalService.failedWithdrawalsPerProduct(productType);
    }
}