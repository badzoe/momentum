package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/investor")
public class InvestorController {

    private final InvestorService investorService;

    // Constructor Injection
    @Autowired
    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping
    public List<Investor> getAllInvestors(){
        return investorService.getAllInvestors();
    }

    // other necessary code
}
