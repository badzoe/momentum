package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.services.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class WithdrawalControllerTest {

    @Mock
    private WithdrawalService withdrawalService;

    @InjectMocks
    private WithdrawalController withdrawalController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWithdrawal() {
        // Arrange
        String productId = "1";
        String amount = "1000";
        when(withdrawalService.processWithdrawal(anyLong(), any(BigDecimal.class)))
                .thenReturn(new ResponseEntity<>("Withdrawal successful", HttpStatus.OK));

        // Act
        ResponseEntity<String> response = withdrawalController.createWithdrawal(productId, amount);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Withdrawal successful", response.getBody());
    }

    @Test
    public void testCreateWithdrawalInvalidAmount() {
        // Arrange
        String productId = "1";
        String amount = "invalid_amount"; // This should cause an error
        when(withdrawalService.processWithdrawal(anyLong(), any(BigDecimal.class)))
                .thenReturn(new ResponseEntity<>("Invalid amount", HttpStatus.BAD_REQUEST));

        // Act & Assert
        try {
            withdrawalController.createWithdrawal(productId, amount);
        } catch (NumberFormatException e) {
            assertEquals("For input string: \"invalid_amount\"", e.getMessage());
        }
    }
}
