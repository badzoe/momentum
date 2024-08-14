package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestProduct;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.InvestorProducts;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Product;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final InvestorRepository investorRepository;

    public List<InvestorProducts> getProductsByInvestorId(Long investorsId){
        return productRepository.findByInvestorId(investorsId).stream()
                .map(investorProduct -> new InvestorProducts(investorProduct.getId(),investorProduct.getType().name(), investorProduct.getName(),investorProduct.getBalance()))
                .collect(Collectors.toList());

    }

    public ResponseEntity<String> addProductsPerInvestor(InvestProduct investProduct){

        Optional<Investor> optionalInvestor =  investorRepository.findById(investProduct.investor());
        if(optionalInvestor.isPresent()){
            Optional<Product> optionalProduct = productRepository.findFirstByTypeAndInvestor_IdOrderByIdDesc(ProductType.valueOf(investProduct.type()),investProduct.investor());
            if(optionalProduct.isPresent()){
                Product productToUpdate = optionalProduct.get();
                productToUpdate.setBalance(productToUpdate.getBalance().add(investProduct.balance()));
                productRepository.save(productToUpdate);
                return ResponseEntity.ok("Successfully updated the product balance");

            }else {
                Product newProduct = new Product();
                newProduct.setId(0L);
                newProduct.setName(investProduct.name());
                newProduct.setType(ProductType.valueOf(investProduct.type()));
                newProduct.setBalance(investProduct.balance());
                newProduct.setInvestor(optionalInvestor.get());
                productRepository.save(newProduct);
                return ResponseEntity.ok("Successfully created a product");
            }
        }else {
            return ResponseEntity.ok("Investor doesn't exist");
        }
    }
}