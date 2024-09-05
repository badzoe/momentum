package com.MomentumInvestments.MomentumInvestmentsApplication.controller;


import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestProductRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.InvestorProductsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/products")
@Tag(name="Products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/investor/{investorId}")
    @Operation(summary = "Get All Products for an investor")
    public List<InvestorProductsResponse> getProductsByInvestorId(@PathVariable Long investorId) {
        return productService.getProductsByInvestorId(investorId);
    }

    @GetMapping()
    @Operation(summary = "Get All Products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return productService.getAllProducts();
    }
    @PostMapping("/addInvestorInvestment")
    @Operation(summary = "Add Products that belong to individual investor")
    public ResponseEntity<String> addProductsPerInvestor(@RequestBody final InvestProductRequest investProductRequest) {
        return productService.addProductsPerInvestor(investProductRequest);
    }

    @PostMapping("/{productName}")
    @Operation(summary = "Add Products")
    public ResponseEntity<String> addProducts(@PathVariable  String productName) {
        return productService.addProducts(productName);
    }
}