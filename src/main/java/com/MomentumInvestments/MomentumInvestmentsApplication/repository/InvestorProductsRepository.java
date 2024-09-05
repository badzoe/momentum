package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.InvestorProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvestorProductsRepository extends JpaRepository<InvestorProducts,Long> {
    List<InvestorProducts> findByProductID_TypeOrderByIdAsc(ProductType type);
    List<InvestorProducts> findByInvestorID_IdOrderByIdAsc(long id);
    Optional<InvestorProducts> findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(long id, long id1);
}