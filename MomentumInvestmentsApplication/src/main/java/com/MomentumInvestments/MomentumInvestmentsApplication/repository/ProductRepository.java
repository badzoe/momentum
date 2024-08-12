package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByInvestorId(Long investorId);
}
