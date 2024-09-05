package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorAuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.InvestorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class InvestorControllerTest {

    @InjectMocks
    private InvestorController investorController;

    @Mock
    private InvestorService investorService;

    @Mock
    private BindingResult result;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInvestors() {
        List<Investor> investors = new ArrayList<>();
        investors.add(new Investor());
        when(investorService.getAllInvestors()).thenReturn(investors);

        List<Investor> result = investorController.getAllInvestors();
        assertEquals(1, result.size());
    }

    @Test
    void testAuthenticateAndGetToken() {
        InvestorAuthenticationRequest request = new InvestorAuthenticationRequest("email@example.com", "password");
        when(investorService.authenticateInvestor(request)).thenReturn(new ResponseEntity<>("token", HttpStatus.OK));

        ResponseEntity<String> response = investorController.authenticateAndGetToken(request,result);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody());
    }

    @Test
    void testAddNewUser() {
        InvestorCreation creation = new InvestorCreation("John", "Doe", null, "address", "phone", "email", "password");
        when(investorService.createInvestor(creation)).thenReturn(new ResponseEntity<>("Investor created", HttpStatus.CREATED));

        ResponseEntity<String> response = investorController.addNewUser(creation);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Investor created", response.getBody());
    }
}
