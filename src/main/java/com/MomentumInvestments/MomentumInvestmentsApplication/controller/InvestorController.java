package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorAuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.InvestorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investor")
@AllArgsConstructor
@Slf4j
@Tag(name="Investor")
public class InvestorController {

    private final InvestorService investorService;
    private static final Logger logger = LoggerFactory.getLogger(InvestorController.class);

    @GetMapping
    @Operation(summary = "Get All Investor")
    public List<Investor> getAllInvestors(){
         logger.info("Fetching all investors");
        List<Investor> investors = investorService.getAllInvestors();
        logger.debug("Number of investors fetched: {}", investors.size());
         return investorService.getAllInvestors();
    }

    @GetMapping("/{investorID}")
    @Operation(summary = "Get Investor By ID")
    public Investor getInvestorByID(@PathVariable Long investorID){
        logger.info("Fetching investor with ID: {}", investorID);
        return investorService.getInvestorByID(investorID);
    }

    @GetMapping("investorBy/{productType}")
    @Operation(summary = "Get Investors By Product Type")
    public List<Investor> getInvestorsByProductType(@PathVariable String productType) {
        logger.info("Fetching investors for product type: {}", productType);

        List<Investor> investors = investorService.getInvestorsByProductType(productType);

        logger.info("Retrieved {} investors for product type: {}", investors.size(), productType);
        logger.debug("Investors details: {}", investors);

        return investors;
    }

    @PostMapping(path = "/login")
    @Operation(summary = "Login & Get JWT Token used to access all tokens")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody @Valid final InvestorAuthenticationRequest authRequest, BindingResult result) {
        logger.info("Authentication attempt for email: {}", authRequest.getEmail());

        if (result.hasErrors()) {
            logger.warn("Validation errors occurred during login attempt for email: {}. Errors: {}", authRequest.getEmail(), result.getAllErrors());
            return ResponseEntity.badRequest().body(result.getAllErrors().toString());
        }

        ResponseEntity<String> response = investorService.authenticateInvestor(authRequest);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Authentication successful for email: {}", authRequest.getEmail());
        } else {
            logger.warn("Authentication failed for email: {}", authRequest.getEmail());
        }

        return response;
    }

    @PostMapping("/register")
    @Operation(summary = "Register Investor in the system")
    public ResponseEntity<String> addNewUser(@RequestBody InvestorCreation investor) {
        logger.info("Received request to register a new investor: {}", investor.email());

        // Continue with registration process
        return investorService.createInvestor(investor);
    }


}