package com.MomentumInvestments.MomentumInvestmentsApplication.controller;


import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/investor/{investorId}")
    public List<Product> getProductsByInvestorId(@PathVariable Long investorId) {
        return productService.getProductsByInvestorId(investorId);
    }
}
