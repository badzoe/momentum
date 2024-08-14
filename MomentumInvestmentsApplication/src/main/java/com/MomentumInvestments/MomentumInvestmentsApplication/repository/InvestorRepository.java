package com.MomentumInvestments.MomentumInvestmentsApplication.repository;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
public interface InvestorRepository extends JpaRepository<Investor, Long> {
    @Query("select c from Investor c where c.email = ?1 order by c.id DESC")
    Optional<Investor> findByName(String username);
}