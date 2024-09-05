package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestProductRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.InvestorProductsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsByInvestorId() {
        Long investorId = 1L;
        List<InvestorProductsResponse> mockResponse = new ArrayList<>();

        when(productService.getProductsByInvestorId(investorId)).thenReturn(mockResponse);

        List<InvestorProductsResponse> result = productController.getProductsByInvestorId(investorId);

        assertEquals(mockResponse, result);
        verify(productService, times(1)).getProductsByInvestorId(investorId);
    }

    @Test
    void testGetAllProducts() {
        List<Product> mockProducts = new ArrayList<>();
        ResponseEntity<List<Product>> mockResponse = ResponseEntity.ok(mockProducts);

        when(productService.getAllProducts()).thenReturn(mockResponse);

        ResponseEntity<List<Product>> result = productController.getAllProducts();

        assertEquals(mockResponse, result);
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testAddProductsPerInvestor() {
        InvestProductRequest investProductRequest = new InvestProductRequest(1L, 1L, BigDecimal.valueOf(1000));
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");

        when(productService.addProductsPerInvestor(investProductRequest)).thenReturn(mockResponse);

        ResponseEntity<String> result = productController.addProductsPerInvestor(investProductRequest);

        assertEquals(mockResponse, result);
        verify(productService, times(1)).addProductsPerInvestor(investProductRequest);
    }

    @Test
    void testAddProducts() {
        String productName = "Product1";
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Product added");

        when(productService.addProducts(productName)).thenReturn(mockResponse);

        ResponseEntity<String> result = productController.addProducts(productName);

        assertEquals(mockResponse, result);
        verify(productService, times(1)).addProducts(productName);
    }
}