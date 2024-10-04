package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.config.security.JwtService;
import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorAuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorProductsRepository;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InvestorService {
    private final InvestorProductsRepository investorProductsRepository;

    private InvestorRepository investorRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;



    public List<Investor> getAllInvestors() {
        return investorRepository.findAll();
    }
    public Investor getInvestorByID(Long investorID) {
        return investorRepository.findById(investorID).get();
    }

    public List<Investor> getInvestorsByProductType(@PathVariable String productType){
        return investorProductsRepository.findByProductID_TypeOrderByIdAsc(ProductType.valueOf(productType))
                .stream()
                .map(investorProducts -> investorProducts.getInvestorID())
                .collect(Collectors.toList());
    }
    public ResponseEntity<String> authenticateInvestor(@RequestBody final InvestorAuthenticationRequest authRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(),authRequest.password()));
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.username());
                return ResponseEntity.ok(token);

            } else {
                return ResponseEntity.badRequest().body("Credentials are not valid");
            }}catch (Exception ex){
            log.info(ex.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }
    }
    public ResponseEntity<String> createInvestor (InvestorCreation userInfo){
        Optional<Investor> checkedUser = investorRepository.findByName(userInfo.email());
        if(checkedUser.isPresent()){
            return ResponseEntity.ok("User Already Registered");
        }else {
            Investor newInvestor = new Investor();
            newInvestor.setId(0);
            newInvestor.setName(userInfo.name());
            newInvestor.setSurname(userInfo.surname());
            newInvestor.setDateOfBirth(userInfo.dateOfBirth());
            newInvestor.setPhoneNumber(userInfo.phoneNumber());
            newInvestor.setAddress(userInfo.address());
            newInvestor.setEmail(userInfo.email());
            newInvestor.setPassword(encoder.encode(userInfo.password()));
            newInvestor.setRole("Investor");
            investorRepository.save(newInvestor);
            return ResponseEntity.ok("Investor Added Successfully");
        }
    }
}