package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.services.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<String> createWithdrawal(@RequestParam Long productId, @RequestParam BigDecimal amount) {
      withdrawalService.processWithdrawal(productId, amount);
      return ResponseEntity.ok("Withdrawal processed successfully");
    }
}
