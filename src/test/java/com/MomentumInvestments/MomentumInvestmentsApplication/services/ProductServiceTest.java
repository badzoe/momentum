package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestProductRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.InvestorProductsResponse;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.InvestorProducts;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorProductsRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InvestorProductsRepository investorProductsRepository;

    @Mock
    private InvestorRepository investorRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductsByInvestorId() {
        InvestorProducts investorProducts = new InvestorProducts();
        investorProducts.setId(1L);
        Product product = new Product();
        product.setType(ProductType.SAVINGS);
        investorProducts.setProductID(product);
        investorProducts.setBalance(BigDecimal.valueOf(1000));

        when(investorProductsRepository.findByInvestorID_IdOrderByIdAsc(1L))
                .thenReturn(List.of(investorProducts));

        List<InvestorProductsResponse> result = productService.getProductsByInvestorId(1L);

        assertEquals(1, result.size());
        assertEquals("SAVINGS", result.get(0).type());
        assertEquals(BigDecimal.valueOf(1000), result.get(0).balance());
    }

    @Test
    void testGetAllProducts() {
        Product product1 = new Product();
        Product product2 = new Product();

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        ResponseEntity<List<Product>> response = productService.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testAddProducts_ProductExists() {
        when(productRepository.findFirstByTypeOrderByIdDesc(ProductType.SAVINGS))
                .thenReturn(Optional.of(new Product()));

        ResponseEntity<String> response = productService.addProducts("SAVINGS");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product already exists", response.getBody());
    }

    @Test
    void testAddProducts_ProductDoesNotExist() {
        when(productRepository.findFirstByTypeOrderByIdDesc(ProductType.SAVINGS))
                .thenReturn(Optional.empty());

        ResponseEntity<String> response = productService.addProducts("SAVINGS");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully added product", response.getBody());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testAddProductsPerInvestor_InvestorAndProductExist_UpdateBalance() {
        Investor investor = new Investor();
        Product product = new Product();
        InvestorProducts existingInvestorProducts = new InvestorProducts();
        existingInvestorProducts.setBalance(BigDecimal.valueOf(1000));

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(2L, 1L))
                .thenReturn(Optional.of(existingInvestorProducts));

        InvestProductRequest request = new InvestProductRequest(1L, 2L, BigDecimal.valueOf(500));
        ResponseEntity<String> response = productService.addProductsPerInvestor(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully updated the investors product balance", response.getBody());
        assertEquals(BigDecimal.valueOf(1500), existingInvestorProducts.getBalance());
        verify(investorProductsRepository, times(1)).save(existingInvestorProducts);
    }

    @Test
    void testAddProductsPerInvestor_InvestorAndProductExist_SaveNew() {
        Investor investor = new Investor();
        Product product = new Product();

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(2L, 1L))
                .thenReturn(Optional.empty());

        InvestProductRequest request = new InvestProductRequest(1L, 2L, BigDecimal.valueOf(500));
        ResponseEntity<String> response = productService.addProductsPerInvestor(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully saved investor product", response.getBody());
        verify(investorProductsRepository, times(1)).save(any(InvestorProducts.class));
    }

    @Test
    void testAddProductsPerInvestor_ProductDoesNotExist() {
        when(investorRepository.findById(1L)).thenReturn(Optional.of(new Investor()));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        InvestProductRequest request = new InvestProductRequest(1L, 2L, BigDecimal.valueOf(500));
        ResponseEntity<String> response = productService.addProductsPerInvestor(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product doesn't exist", response.getBody());
    }

    @Test
    void testAddProductsPerInvestor_InvestorDoesNotExist() {
        when(investorRepository.findById(1L)).thenReturn(Optional.empty());

        InvestProductRequest request = new InvestProductRequest(1L, 2L, BigDecimal.valueOf(500));
        ResponseEntity<String> response = productService.addProductsPerInvestor(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Investor doesn't exist", response.getBody());
    }
}
