package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.MicroServiceRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.WithdrawalsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.*;
import com.MomentumInvestments.MomentumInvestmentsApplication.exception.ValidationException;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WithdrawalService {
    private final ProductRepository productRepository;
    private final InvestorRepository investorRepository;
    private final InvestorProductsRepository investorProductsRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final AuditTrailRepository auditTrailRepository;
    private final RabbitMQService rabbitMQService;
    private final EmailListRepository emailListRepository;

    public ResponseEntity<List<WithdrawalsResponse>> successfulWithdrawalsPerProduct (String productType ){

        return ResponseEntity.ok(withdrawalRepository.findByProduct_ProductID_TypeAndStatusOrderByIdAsc(ProductType.valueOf(productType),"DONE")
                .stream()
                .map(withdrawal -> new WithdrawalsResponse(withdrawal.getId(),productType.toUpperCase(), withdrawal.getStatus(), withdrawal.getAmount(),withdrawal.getTimestamp(),withdrawal.getProduct().getInvestorID().getName()+" "+ withdrawal.getProduct().getInvestorID().getSurname()) )
                .collect(Collectors.toList()));

    }

    public ResponseEntity<List<WithdrawalsResponse>> failedWithdrawalsPerProduct (String productType ){

        return ResponseEntity.ok(withdrawalRepository.findByProduct_ProductID_TypeAndStatusContainsOrderByIdAsc(ProductType.valueOf(productType),"FAILED")
                .stream()
                .map(withdrawal -> new WithdrawalsResponse(withdrawal.getId(),productType.toUpperCase(), withdrawal.getStatus(), withdrawal.getAmount(),withdrawal.getTimestamp(),withdrawal.getProduct().getInvestorID().getName()+" "+ withdrawal.getProduct().getInvestorID().getSurname()) )
                .collect(Collectors.toList()));

    }

    @Transactional
    public ResponseEntity<String> processWithdrawal(Long investorID, Long productId, BigDecimal amount) throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        Optional<Investor> optionalInvestor = investorRepository.findById(investorID);
        if (optionalInvestor.isEmpty()) {

            return ResponseEntity.badRequest().body("Investor not registered");
        }

        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not registered");
        }

        Optional<InvestorProducts> optionalInvestorProducts = investorProductsRepository
                .findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(productId, investorID);

        if (optionalInvestorProducts.isEmpty()) {
            return ResponseEntity.badRequest().body("Investor doesn't have an investment that matches the provided details");
        }

        ResponseEntity<String> validationResponse = validateWithdrawal(optionalInvestor.get(), optionalProduct.get(), optionalInvestorProducts.get(), amount);
        if (validationResponse != null) {
            return validationResponse;
        }

        return executeWithdrawal(optionalInvestorProducts.get(), amount);
    }


    private ResponseEntity<String> validateWithdrawal(Investor investor, Product product, InvestorProducts investorProducts, BigDecimal amount) {
        // Create a base Withdrawal for potential error cases
        Withdrawal errorWithdrawal = createBaseWithdrawal(investorProducts, amount);

        // Validate retirement product and age condition
        if (isRetirementWithdrawalDisallowed(investor, product)) {
            return handleFailedWithdrawal(errorWithdrawal, "Investor cannot perform withdrawal on " + ProductType.RETIREMENT.name() + " when age is less than 65");
        }

        // Validate balance is sufficient for withdrawal
        if (isWithdrawalExceedingBalance(investorProducts, amount)) {
            return handleFailedWithdrawal(errorWithdrawal, "Investor cannot withdraw an amount greater than the current balance");
        }

        // Validate withdrawal is within 90% of the current balance
        if (isWithdrawalExceeding90Percent(investorProducts, amount)) {
            return handleFailedWithdrawal(errorWithdrawal, "Investor cannot withdraw an amount greater than 90% of the current balance");
        }

        return null; // Validation passed
    }

    private Withdrawal createBaseWithdrawal(InvestorProducts investorProducts, BigDecimal amount) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(0L);
        withdrawal.setAmount(amount);
        withdrawal.setProduct(investorProducts);
        return withdrawal;
    }

    private boolean isRetirementWithdrawalDisallowed(Investor investor, Product product) {
        return product.getType().equals(ProductType.RETIREMENT) &&
                Period.between(LocalDate.parse(investor.getDateOfBirth()), LocalDate.now()).getYears() < 65;
    }

    private boolean isWithdrawalExceedingBalance(InvestorProducts investorProducts, BigDecimal amount) {
        return investorProducts.getBalance().compareTo(amount) < 0;
    }

    private boolean isWithdrawalExceeding90Percent(InvestorProducts investorProducts, BigDecimal amount) {
        return investorProducts.getBalance().multiply(new BigDecimal("0.9")).compareTo(amount) < 0;
    }

    private ResponseEntity<String> handleFailedWithdrawal(Withdrawal errorWithdrawal, String failureMessage) {
        errorWithdrawal.setTimestamp(LocalDateTime.now());
        errorWithdrawal.setStatus("FAILED - " + failureMessage);
        withdrawalRepository.save(errorWithdrawal);
        return ResponseEntity.badRequest().body(failureMessage);
    }

    private ResponseEntity<String> executeWithdrawal(InvestorProducts investorProducts, BigDecimal amount) throws ExecutionException, InterruptedException, TimeoutException {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setProduct(investorProducts);
        withdrawal.setAmount(amount);
        withdrawal.setTimestamp(LocalDateTime.now());
        withdrawal.setStatus("STARTED");
        withdrawalRepository.save(withdrawal);

        updateWithdrawalStatus(withdrawal, "EXECUTING");

        // Deduct the amount from the product balance
        BigDecimal previousBalance = investorProducts.getBalance();
        investorProducts.setBalance(previousBalance.subtract(amount));
        investorProductsRepository.save(investorProducts);

        createAuditTrail(investorProducts.getId(), previousBalance, investorProducts.getBalance());
        updateWithdrawalStatus(withdrawal, "DONE");

        createAuditTrailForWithdrawalStatus(withdrawal.getId(), "EXECUTING", "DONE");
        EmailList email = new EmailList();
        email.setId(0l);
        email.setToEmail("tapschibz@gmail.com");
        email.setTitle("TRANSACTION STATUS");
        email.setBodyMessage("Successfully performed withdrawal on your account");

        EmailList savedEmail = emailListRepository.save(email);
        rabbitMQService.dispatchToQueue(
                new MicroServiceRequest(
                        savedEmail.getId(),
                        savedEmail.getToEmail(),
                        savedEmail.getTitle(),
                        savedEmail.getBodyMessage(),
                        "SUCCESS",
                        LocalDateTime.now(),
                        LocalDateTime.now()

        ),rabbitMQService.getCorrelationData());

        return ResponseEntity.ok("Successful withdrawal");
    }

    private void updateWithdrawalStatus(Withdrawal withdrawal, String status) {
        withdrawal.setStatus(status);
        withdrawalRepository.save(withdrawal);
    }

    private void createAuditTrail(Long productId, BigDecimal previousBalance, BigDecimal newBalance) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setProductId(productId);
        auditTrail.setPreviousBalance(previousBalance);
        auditTrail.setNewBalance(newBalance);
        auditTrailRepository.save(auditTrail);
    }

    private void createAuditTrailForWithdrawalStatus(Long withdrawalId, String previousStatus, String newStatus) {
        AuditTrail withdrawalAuditTrail = new AuditTrail();
        withdrawalAuditTrail.setWithdrawalId(withdrawalId);
        withdrawalAuditTrail.setPreviousStatus(previousStatus);
        withdrawalAuditTrail.setNewStatus(newStatus);
        auditTrailRepository.save(withdrawalAuditTrail);
    }
}