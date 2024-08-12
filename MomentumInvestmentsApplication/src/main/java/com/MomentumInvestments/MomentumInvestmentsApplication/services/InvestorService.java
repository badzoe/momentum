package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestorService {

    @Autowired
    private InvestorRepository investorRepository;
    public List<Investor> getAllInvestors() {
        return investorRepository.findAll();
    }
    public void saveInvestor(Investor investor){

    }
}
