package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
