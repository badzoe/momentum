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
    private final InvestorProductsRepository investorProductsRepository;
    private final InvestorRepository investorRepository;

    public List<InvestorProductsResponse> getProductsByInvestorId(Long investorsId){
        return investorProductsRepository.findByInvestorID_IdOrderByIdAsc(investorsId).stream()
                .map(investorProduct -> new InvestorProductsResponse(investorProduct.getId(),investorProduct.getProductID().getType().name(), investorProduct.getProductID().getType().name(),investorProduct.getBalance()))
                .collect(Collectors.toList());

    }

    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(productRepository.findAll());
    }

    public ResponseEntity<String> addProducts(String name) {
        Optional<Product> optionalProduct = productRepository.findFirstByTypeOrderByIdDesc(ProductType.valueOf(name));
        if(optionalProduct.isPresent()){
            return ResponseEntity.badRequest().body("Product already exists");

        }else {
            Product productToBeAdded = new Product();
            productToBeAdded.setId(0L);
            productToBeAdded.setType(ProductType.valueOf(name));
            productRepository.save(productToBeAdded);
            return ResponseEntity.ok("Successfully added product");
        }
    }

    public ResponseEntity<String> addProductsPerInvestor(InvestProductRequest investProduct){

        Optional<Investor> optionalInvestor =  investorRepository.findById(investProduct.investorID());
        if(optionalInvestor.isPresent()){
            Optional<Product> optionalProduct =  productRepository.findById(investProduct.productID());
            if(optionalProduct.isPresent()){
                Optional<InvestorProducts> optionalInvestorProducts = investorProductsRepository.findFirstByProductID_IdAndInvestorID_IdOrderByIdDesc(investProduct.productID(), investProduct.investorID());
                if(optionalInvestorProducts.isPresent()){
                    InvestorProducts investorProductsToUpdate = optionalInvestorProducts.get();
                    investorProductsToUpdate.setBalance(investorProductsToUpdate.getBalance().add(investProduct.balance()));
                    investorProductsRepository.save(investorProductsToUpdate);
                    return ResponseEntity.ok("Successfully updated the investors product balance");

                }else {
                    InvestorProducts newInvestorProducts = new InvestorProducts();
                    newInvestorProducts.setId(0L);
                    newInvestorProducts.setInvestorID(optionalInvestor.get());
                    newInvestorProducts.setProductID(optionalProduct.get());
                    newInvestorProducts.setBalance(investProduct.balance());
                    investorProductsRepository.save(newInvestorProducts);
                    return ResponseEntity.ok("Successfully saved investor product");


                }

            }else {
                return ResponseEntity.badRequest().body("Product doesn't exist");
            }

        }else {
            return ResponseEntity.ok("Investor doesn't exist");
        }
    }
}