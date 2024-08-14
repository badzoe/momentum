package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.AuditTrail;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Withdrawal;
import com.MomentumInvestments.MomentumInvestmentsApplication.exception.ValidationException;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.AuditTrailRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.ProductRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.WithdrawalRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class WithdrawalService {
    private final ProductRepository productRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final AuditTrailRepository auditTrailRepository;

    @Transactional
    public ResponseEntity<String> processWithdrawal(Long productId, BigDecimal amount) throws ValidationException {
        Optional<Product> product = productRepository.findById(productId);
        if(product.isPresent()){
            if(product.get().getType().equals(ProductType.RETIREMENT) && Period.between(product.get().getInvestor().getDateOfBirth(),LocalDate.now()).getYears() < 65){
                return ResponseEntity.badRequest().body("Investor cannot perform withdrawal on "+ ProductType.RETIREMENT.name()+" when age is less than 65");
            }else if (product.get().getBalance().compareTo(amount)< 0 ){
                return ResponseEntity.badRequest().body("Investor cannot withdraw amount greater than current balance");
            }else if(product.get().getBalance().multiply(new BigDecimal("0.9")).compareTo(amount) < 0 ){
                return ResponseEntity.badRequest().body("Investor cannot withdraw an amount 90% + of the current balance");

            }else {
                try {


                    Withdrawal withdrawal = new Withdrawal();
                    withdrawal.setProduct(product.get());
                    withdrawal.setAmount(amount);
                    withdrawal.setTimestamp(LocalDateTime.now());
                    withdrawal.setStatus("STARTED");
                    withdrawalRepository.save(withdrawal);

                    // Send an internal event to move the process to EXECUTING status
                    withdrawal.setStatus("EXECUTING");
                    withdrawalRepository.save(withdrawal);

                    // Deduct the amount from the product balance
                    product.get().setBalance(product.get().getBalance().subtract(amount));
                    productRepository.save(product.get());

                    // Create an audit trail for the product balance change
                    AuditTrail auditTrail = new AuditTrail();
                    auditTrail.setProductId(product.get().getId());
                    auditTrail.setPreviousBalance(product.get().getBalance().add(amount));
                    auditTrail.setNewBalance(product.get().getBalance());
                    auditTrailRepository.save(auditTrail);

                    // Move the process to DONE status
                    withdrawal.setStatus("DONE");
                    withdrawalRepository.save(withdrawal);

                    // Create an audit trail for the withdrawal process status change
                    AuditTrail withdrawalAuditTrail = new AuditTrail();
                    withdrawalAuditTrail.setWithdrawalId(withdrawal.getId());
                    withdrawalAuditTrail.setPreviousStatus("EXECUTING");
                    withdrawalAuditTrail.setNewStatus("DONE");
                    auditTrailRepository.save(withdrawalAuditTrail);

                    return ResponseEntity.ok("Successful withdrawal");

                }catch (Exception ex){
                    log.error(ex.toString());
                    return ResponseEntity.internalServerError().body(ex.getMessage());
                }
            }
        }else{
            return ResponseEntity.badRequest().body("Product not found");
        }
    }
}