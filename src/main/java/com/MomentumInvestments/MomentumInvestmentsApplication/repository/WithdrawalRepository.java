package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByProduct_ProductID_TypeAndStatusContainsOrderByIdAsc(ProductType type, String status);

    List<Withdrawal> findByProduct_ProductID_TypeAndStatusOrderByIdAsc(ProductType type, String status);
}