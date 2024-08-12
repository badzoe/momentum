package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
