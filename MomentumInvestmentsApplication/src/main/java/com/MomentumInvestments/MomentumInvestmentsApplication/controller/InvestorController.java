package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorAuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.InvestorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investor")
@AllArgsConstructor
@Slf4j
@Tag(name="Investor")
public class InvestorController {

    private final InvestorService investorService;

    @GetMapping
    @Operation(summary = "Get All Investor")
    public List<Investor> getAllInvestors(){
        return investorService.getAllInvestors();
    }

    @GetMapping("/{investorID}")
    @Operation(summary = "Get Investor By ID")
    public Investor getInvestorByID(@PathVariable Long investorID){
        return investorService.getInvestorByID(investorID);
    }

    @GetMapping("investorBy/{productType}")
    @Operation(summary = "Get Investors By Product Type")
    public List<Investor> getInvestorsByProductType(@PathVariable String productType){
        return investorService.getInvestorsByProductType(productType);
    }

    @PostMapping(path = "/login")
    @Operation(summary = "Login & Get JWT Token used to access all tokens")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody final InvestorAuthenticationRequest authRequest) {
        return investorService.authenticateInvestor(authRequest);
    }

    @PostMapping("/register")
    @Operation(summary = "Register Investor in the system")
    public ResponseEntity<String> addNewUser(@RequestBody InvestorCreation investor) {
        return investorService.createInvestor(investor);
    }
}