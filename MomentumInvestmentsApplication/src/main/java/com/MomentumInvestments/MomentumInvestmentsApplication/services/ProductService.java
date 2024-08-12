package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByInvestorId(Long investorsId){
        return productRepository.findByInvestorId(investorsId);
    }
    // other necessary methods
}
