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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvestorControllerTest {

    @InjectMocks
    private InvestorController investorController;

    @Mock
    private InvestorService investorService;

    @Mock
    private Model model;

    @Mock
    private BindingResult result;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInvestors() {
        List<Investor> investors = new ArrayList<>();
        investors.add(new Investor());
        when(investorService.getAllInvestors()).thenReturn(investors);

        List<Investor> result = (List<Investor>) investorController.getAllInvestors();
        assertEquals(1, result.size());
    }

    @Test
    void testAuthenticateAndGetToken_Success() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        InvestorAuthenticationRequest authRequest = new InvestorAuthenticationRequest(email, password);
        ResponseEntity<String> successResponse = new ResponseEntity<>("JWT_Token", HttpStatus.OK);

        when(investorService.authenticateInvestor(authRequest)).thenReturn(successResponse);

        // Act
        String result = investorController.authenticateAndGetToken(email, password, model);

        // Assert
        verify(investorService).authenticateInvestor(authRequest); // Verify service was called
        assertEquals("redirect:/withdraw", result); // Expect redirection to withdrawal page on success
    }

    @Test
    void testAuthenticateAndGetToken_Failure() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongpassword";
        InvestorAuthenticationRequest authRequest = new InvestorAuthenticationRequest(email, password);
        ResponseEntity<String> failureResponse = new ResponseEntity<>("Authentication Failed", HttpStatus.UNAUTHORIZED);

        when(investorService.authenticateInvestor(authRequest)).thenReturn(failureResponse);

        // Act
        String result = investorController.authenticateAndGetToken(email, password, model);

        // Assert
        verify(investorService).authenticateInvestor(authRequest); // Verify service was called
        verify(model).addAttribute("errorMessage", "Authentication failed. Please try again."); // Verify the model was updated with the error message
        assertEquals("login", result); // Expect to return to login page on failure
    }



    @Test
    void testAddNewUser_Success() {
        // Arrange
        InvestorCreation investor = new InvestorCreation("john", "doe", "200-01-01", "41 rinyani ave", "0652873902", "test@test.com", "1234");
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<String> successResponse = new ResponseEntity<>("Investor Created", HttpStatus.OK);
        when(investorService.createInvestor(investor)).thenReturn(successResponse);

        // Act
        String result = investorController.addNewUser(investor, bindingResult, model);

        // Assert
        verify(model).addAttribute("message", "Save successful"); // Verify the success message
        assertEquals("redirect:/registration-success", result); // Expect redirection to the success page
    }

    @Test
    void testAddNewUser_SaveFailed() {
        // Arrange
        InvestorCreation investor = new InvestorCreation("john", "doe", "200-01-01", "41 rinyani ave", "0652873902", "test@test.com", "1234");
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<String> failureResponse = new ResponseEntity<>("Save Failed", HttpStatus.BAD_REQUEST);
        when(investorService.createInvestor(investor)).thenReturn(failureResponse);

        // Act
        String result = investorController.addNewUser(investor, bindingResult, model);

        // Assert
        verify(model).addAttribute("message", "Save failed"); // Verify the failure message
        assertEquals("RegisterInvestor", result); // Should return the registration form on save failure
    }
}
