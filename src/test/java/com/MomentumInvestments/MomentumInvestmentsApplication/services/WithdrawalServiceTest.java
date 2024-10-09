package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.WithdrawalsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.*;
import com.MomentumInvestments.MomentumInvestmentsApplication.exception.ValidationException;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WithdrawalServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private InvestorProductsRepository investorProductsRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private AuditTrailRepository auditTrailRepository;

    @InjectMocks
    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSuccessfulWithdrawalsPerProduct() {
        Product product = new Product();
        product.setType(ProductType.SAVINGS);
        Investor investor = new Investor();
        investor.setName("John");
        investor.setSurname("Doe");
        InvestorProducts investorProducts = new InvestorProducts();
        investorProducts.setInvestorID(investor);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(1L);
        withdrawal.setStatus("DONE");
        withdrawal.setAmount(BigDecimal.valueOf(1000));
        withdrawal.setTimestamp(LocalDateTime.now());
        withdrawal.setProduct(investorProducts);

        when(withdrawalRepository.findByProduct_ProductID_TypeAndStatusOrderByIdAsc(ProductType.SAVINGS, "DONE"))
                .thenReturn(List.of(withdrawal));

        ResponseEntity<List<WithdrawalsResponse>> response = withdrawalService.successfulWithdrawalsPerProduct("SAVINGS");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).investorName());
        assertEquals("DONE", response.getBody().get(0).status());
    }

    @Test
    void testFailedWithdrawalsPerProduct() {
        Product product = new Product();
        product.setType(ProductType.SAVINGS);
        Investor investor = new Investor();
        investor.setName("John");
        investor.setSurname("Doe");
        InvestorProducts investorProducts = new InvestorProducts();
        investorProducts.setInvestorID(investor);
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setId(1L);
        withdrawal.setStatus("FAILED");
        withdrawal.setAmount(BigDecimal.valueOf(1000));
        withdrawal.setTimestamp(LocalDateTime.now());
        withdrawal.setProduct(investorProducts);

        when(withdrawalRepository.findByProduct_ProductID_TypeAndStatusContainsOrderByIdAsc(ProductType.SAVINGS, "FAILED"))
                .thenReturn(List.of(withdrawal));

        ResponseEntity<List<WithdrawalsResponse>> response = withdrawalService.failedWithdrawalsPerProduct("SAVINGS");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).investorName());
        assertEquals("FAILED", response.getBody().get(0).status());
    }

    @Test
    void testProcessWithdrawal_InvestorNotRegistered() throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        when(investorRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = withdrawalService.processWithdrawal(1L, 2L, BigDecimal.valueOf(500));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Investor not registered", response.getBody());
    }

    @Test
    void testProcessWithdrawal_ProductNotRegistered() throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(new Investor()));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = withdrawalService.processWithdrawal(1L, 2L, BigDecimal.valueOf(500));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product not registered", response.getBody());
    }

    @Test
    void testProcessWithdrawal_NoMatchingInvestment() throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(new Investor()));
        when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));
        when(investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(2L, 1L))
                .thenReturn(Optional.empty());

        ResponseEntity<String> response = withdrawalService.processWithdrawal(1L, 2L, BigDecimal.valueOf(500));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Investor doesn't have an investment that matches the provided details", response.getBody());
    }

    @Test
    void testProcessWithdrawal_WithdrawalExceedsBalance() throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        Investor investor = new Investor();
        Product product = new Product();
        product.setType(ProductType.SAVINGS);
        InvestorProducts investorProducts = new InvestorProducts();
        investorProducts.setBalance(BigDecimal.valueOf(300));

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(2L, 1L))
                .thenReturn(Optional.of(investorProducts));

        ResponseEntity<String> response = withdrawalService.processWithdrawal(1L, 2L, BigDecimal.valueOf(500));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Investor cannot withdraw an amount greater than the current balance", response.getBody());
        verify(withdrawalRepository, times(1)).save(any(Withdrawal.class));
    }

    @Test
    void testProcessWithdrawal_WithdrawalExceeds90Percent() throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        Investor investor = new Investor();
        Product product = new Product();
        product.setType(ProductType.SAVINGS);
        InvestorProducts investorProducts = new InvestorProducts();
        investorProducts.setBalance(BigDecimal.valueOf(1000));

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(2L, 1L))
                .thenReturn(Optional.of(investorProducts));

        ResponseEntity<String> response = withdrawalService.processWithdrawal(1L, 2L, BigDecimal.valueOf(950));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Investor cannot withdraw an amount greater than 90% of the current balance", response.getBody());
        verify(withdrawalRepository, times(1)).save(any(Withdrawal.class));
    }

    @Test
    void testProcessWithdrawal_Success() throws ValidationException, ExecutionException, InterruptedException, TimeoutException {
        Investor investor = new Investor();
        investor.setDateOfBirth(String.valueOf(LocalDate.parse(LocalDate.now().minusYears(70).toString())));
        Product product = new Product();
        product.setType(ProductType.RETIREMENT);
        InvestorProducts investorProducts = new InvestorProducts();
        investorProducts.setBalance(BigDecimal.valueOf(1000));

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(2L, 1L))
                .thenReturn(Optional.of(investorProducts));

        ResponseEntity<String> response = withdrawalService.processWithdrawal(1L, 2L, BigDecimal.valueOf(500));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successful withdrawal", response.getBody());
        verify(withdrawalRepository, times(3)).save(any(Withdrawal.class));
        verify(investorProductsRepository, times(1)).save(any(InvestorProducts.class));
        verify(auditTrailRepository, times(2)).save(any(AuditTrail.class));
    }
}
