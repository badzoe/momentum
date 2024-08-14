package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByInvestorId(Long investorId);

    Optional<Product> findFirstByTypeAndInvestor_IdOrderByIdDesc(ProductType type, long id);
}