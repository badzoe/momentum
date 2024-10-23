package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.services.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Controller
@AllArgsConstructor
@Tag(name = "Withdrawal")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @GetMapping("/withdraw")
    public String showWithdrawalPage(Model model) {
        return "WithdrawalPage";
    }

    @GetMapping("/withdraw/retirement")
    public String showSavingsWithdrawalPage(Model model) {
        model.addAttribute("welcomeMessage", "Welcome! Please complete your retirement withdrawal request.");
        return "RetirementWithdrawal";
    }

    @GetMapping("/withdraw/savings")
    public String showRetirementWithdrawalPage(Model model) {
        model.addAttribute("welcomeMessage", "Welcome! Please complete your savings withdrawal request.");
        return "SavingsWithdrawal";
    }

    @PostMapping("/withdraw/retirement/process")
    @Operation(summary = "Process Retirement Withdrawal")
    public String processSavingsWithdrawal(
            @RequestParam("investorId") String investorId,
            @RequestParam("productId") String productId,
            @RequestParam("amount") String amount,
            Model model) throws ExecutionException, InterruptedException, TimeoutException {

        // Validate product ID
        String productName;
        if ("22".equals(productId)) { // Assuming '24' is the valid ID for Retirement
            productName = "Retirement";
        } else {
            model.addAttribute("message", "Invalid product ID.");
            return "withdrawalFailure"; // Redirect to failure page if productId is invalid
        }

        try {
            // Convert inputs to appropriate types
            Long investorIdLong = Long.valueOf(investorId);
            BigDecimal withdrawalAmount = new BigDecimal(amount); // Convert amount safely

            // Call the withdrawal service to process the request
            ResponseEntity<String> response = withdrawalService.processWithdrawal(investorIdLong, Long.valueOf(productId), withdrawalAmount);

            // Check response and set message accordingly
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("message", productName + " withdrawal was successful!");
            } else {
                model.addAttribute("message", productName + " withdrawal failed.");
            }
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Invalid amount format. Please enter a valid number.");
            return "withdrawalFailure";
        }

        return "RetirementWithdrawal"; // Redirect to success page on completion
    }


    @PostMapping("/withdraw/savings/process")
    @Operation(summary = "Process Withdrawal")
    public String handleWithdrawal(
            @RequestParam("investorId") String investorId,
            @RequestParam("amount") String amount,
            @RequestParam("productId") String productId, // Ensure this is passed correctly
            Model model) throws ExecutionException, InterruptedException, TimeoutException {

        // Validate product ID
        String productName;
        if ("23".equals(productId)) {
            productName = "Savings";
        } else {
            model.addAttribute("message", "Invalid product ID.");
            return "withdrawalFailure"; // Redirect to failure page if productId is invalid
        }

        try {
            // Convert inputs to appropriate types
            Long investorIdLong = Long.valueOf(investorId);
            BigDecimal withdrawalAmount = new BigDecimal(amount); // Convert amount safely

            // Call the withdrawal service to process the request
            ResponseEntity<String> response = withdrawalService.processWithdrawal(investorIdLong, Long.valueOf(productId), withdrawalAmount);

            // Check response and set message accordingly
            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("message", productName + " withdrawal was successful!");
            } else {
                model.addAttribute("message", productName + " withdrawal failed.");
            }
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Invalid amount format. Please enter a valid number.");
            return "withdrawalFailure";
        }

        return "SavingsWithdrawal"; // Redirect to success page on completion
    }


}
