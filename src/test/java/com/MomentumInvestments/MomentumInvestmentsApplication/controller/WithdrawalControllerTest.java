package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.WithdrawalsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@EnableWebMvc
public class WithdrawalControllerTest {

    @Mock
    private WithdrawalService withdrawalService;

    @InjectMocks
    private WithdrawalController withdrawalController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(withdrawalController).build();
    }

    @Test
    public void testCreateWithdrawal() throws Exception {
        // Mocking the response from the service
        when(withdrawalService.processWithdrawal(anyLong(), anyLong(), any(BigDecimal.class)))
                .thenReturn(ResponseEntity.ok("Successful withdrawal"));

        mockMvc.perform(post("/api/withdrawals/{userId}/{productId}/{amount}", 1, 1, 1000))
                .andExpect(status().isOk())
                .andExpect(content().string("Successful withdrawal"));
    }



    @Test
    public void testSuccessfulWithdrawalsPerProduct() throws Exception {
        // Mocking the response from the service
        WithdrawalsResponse response = new WithdrawalsResponse(1L, "PRODUCT_TYPE", "SUCCESS", BigDecimal.valueOf(1000), null, "Investor Name");
        when(withdrawalService.successfulWithdrawalsPerProduct(anyString()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(response)));

        mockMvc.perform(get("/api/withdrawals/successful/{productType}", "PRODUCT_TYPE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].product").value("PRODUCT_TYPE"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].amount").value(1000))
                .andExpect(jsonPath("$[0].investorName").value("Investor Name"));
    }

    @Test
    public void testFailedWithdrawalsPerProduct() throws Exception {
        // Mocking the response from the service
        WithdrawalsResponse response = new WithdrawalsResponse(2L, "PRODUCT_TYPE", "FAILED", BigDecimal.valueOf(500), null, "Another Investor");
        when(withdrawalService.failedWithdrawalsPerProduct(anyString()))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(response)));

        mockMvc.perform(get("/api/withdrawals/failed/{productType}", "PRODUCT_TYPE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].product").value("PRODUCT_TYPE"))
                .andExpect(jsonPath("$[0].status").value("FAILED"))
                .andExpect(jsonPath("$[0].amount").value(500))
                .andExpect(jsonPath("$[0].investorName").value("Another Investor"));
    }
}