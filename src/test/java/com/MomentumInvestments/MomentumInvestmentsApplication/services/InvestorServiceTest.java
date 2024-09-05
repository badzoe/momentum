package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.config.security.JwtService;
import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorAuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorProductsRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class InvestorServiceTest {

    @Mock
    private InvestorProductsRepository investorProductsRepository;

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InvestorService investorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInvestors() {
        List<Investor> investors = new ArrayList<>();
        investors.add(new Investor());
        when(investorRepository.findAll()).thenReturn(investors);

        List<Investor> result = investorService.getAllInvestors();
        assertEquals(1, result.size());
        assertEquals(investors, result);
    }

    @Test
    void testGetInvestorByID() {
        Investor investor = new Investor();
        investor.setId(1L);
        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));

        Investor result = investorService.getInvestorByID(1L);
        assertEquals(investor, result);
    }

    @Test
    void testGetInvestorByID_NotFound() {
        when(investorRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            investorService.getInvestorByID(1L);
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Investor not found."));
        }
    }

    @Test
    void testGetInvestorsByProductType() {
        List<Investor> investors = new ArrayList<>();
        investors.add(new Investor());
        when(investorProductsRepository.findByProductID_TypeOrderByIdAsc(ProductType.SAVINGS))
                .thenReturn(new ArrayList<>());
        // Adjust the mock as per your logic in the method.

        List<Investor> result = investorService.getInvestorsByProductType("SAVINGS");
        assertEquals(0, result.size());
    }

    @Test
    void testAuthenticateInvestor_Success() {
        InvestorAuthenticationRequest request = new InvestorAuthenticationRequest("username", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken("username")).thenReturn("token");

        ResponseEntity<String> response = investorService.authenticateInvestor(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token", response.getBody());
    }

    @Test
    void testAuthenticateInvestor_Failure() {
        InvestorAuthenticationRequest request = new InvestorAuthenticationRequest("username", "password");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseEntity<String> response = investorService.authenticateInvestor(request);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Credentials are not valid", response.getBody());
    }

    @Test
    void testAuthenticateInvestor_Exception() {
        InvestorAuthenticationRequest request = new InvestorAuthenticationRequest("username", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Exception"));

        ResponseEntity<String> response = investorService.authenticateInvestor(request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCodeValue());
    }

    @Test
    void testCreateInvestor_UserAlreadyRegistered() {
        InvestorCreation request = new InvestorCreation("name", "surname", "dateOfBirth", "address", "phoneNumber", "email", "password");
        when(investorRepository.findByName("email")).thenReturn(Optional.of(new Investor()));

        ResponseEntity<String> response = investorService.createInvestor(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User Already Registered", response.getBody());
    }

    @Test
    void testCreateInvestor_Success() {
        // Create the request object
        InvestorCreation request = new InvestorCreation("name", "surname", "dateOfBirth", "address", "phoneNumber", "email", "password");

        // Mock repository and password encoder behavior
        when(investorRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Call the service method
        ResponseEntity<String> response = investorService.createInvestor(request);

        // Assertions
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Investor Added Successfully", response.getBody());

        // Verify that the repository's safe method was called exactly once
        verify(investorRepository, times(1)).save(any(Investor.class));
    }

}