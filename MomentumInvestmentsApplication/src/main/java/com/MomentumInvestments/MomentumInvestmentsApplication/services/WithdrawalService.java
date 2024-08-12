package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Withdrawal;
import com.MomentumInvestments.MomentumInvestmentsApplication.exception.ValidationException;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.ProductRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WithdrawalService {

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Transactional
    public void processWithdrawal(Long productId, BigDecimal amount) throws ValidationException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ValidationException("Product not found"));

        if (product.getType().equals("RETIREMENT") && investorRepository.findById(product.getInvestor().getId()).get().getDateOfBirth().plusYears(65).isAfter(LocalDate.now())) {
            throw new ValidationException("Investor is not old enough to withdraw from retirement product");
        }

        if (amount.compareTo(product.getBalance()) > 0) {
            throw new ValidationException("Withdrawal amount is greater than current balance");
        }

        if (amount.compareTo(product.getBalance().multiply(BigDecimal.valueOf(0.9))) > 0) {
            throw new ValidationException("Cannot withdraw more than 90% of the balance");
        }

        product.setBalance(product.getBalance().subtract(amount));
        productRepository.save(product);

        product.setBalance(product.getBalance().subtract(amount));
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setProduct(product);
        withdrawal.setAmount(amount);
        withdrawal.setTimestamp(LocalDateTime.now());
        withdrawal.setStatus("STARTED");
        withdrawalRepository.save(withdrawal);



        // Add additional logic to handle EXECUTING and DONE statuses
    }

}
