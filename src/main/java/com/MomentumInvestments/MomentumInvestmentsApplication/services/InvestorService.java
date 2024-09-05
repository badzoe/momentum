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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class InvestorService {

    private final InvestorProductsRepository investorProductsRepository;
    private final InvestorRepository investorRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    public List<Investor> getAllInvestors() {
        log.info("Fetching all investors.");
        return investorRepository.findAll();
    }

    public Investor getInvestorByID(Long id) {
        log.info("Fetching investor by ID: {}", id);
        return investorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investor not found."));
    }

    public List<Investor> getInvestorsByProductType(String productType) {
        log.info("Fetching investors by product type: {}", productType);
        return investorProductsRepository.findByProductID_TypeOrderByIdAsc(ProductType.valueOf(productType))
                .stream()
                .map(investorProducts -> investorProducts.getInvestorID())
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> authenticateInvestor(final InvestorAuthenticationRequest authRequest) {
        try {
            log.info("Authenticating investor with email: {}", authRequest.username());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.username());
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.badRequest().body("Credentials are not valid");
            }
        } catch (Exception ex) {
            log.error("Authentication failed for email: {}", authRequest.username(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed.");
        }
    }

    public ResponseEntity<String> createInvestor(InvestorCreation userInfo) {
        log.info("Creating new investor with email: {}", userInfo.email());
        Optional<Investor> checkedUser = (Optional<Investor>) investorRepository.findByEmail(userInfo.email());
        if (checkedUser.isPresent()) {
            log.warn("Investor with email {} already exists.", userInfo.email());
            return ResponseEntity.ok("User Already Registered");
        } else {
            try {
                LocalDate dateOfBirth = LocalDate.parse(userInfo.dateOfBirth());
                Investor newInvestor = new Investor();
                newInvestor.setName(userInfo.name());
                newInvestor.setSurname(userInfo.surname());
                newInvestor.setDateOfBirth(dateOfBirth.toString());
                newInvestor.setPhoneNumber(userInfo.phoneNumber());
                newInvestor.setAddress(userInfo.address());
                newInvestor.setEmail(userInfo.email());
                newInvestor.setPassword(encoder.encode(userInfo.password()));
                newInvestor.setRole("Investor");

                investorRepository.save(newInvestor);
                log.info("Investor with email {} created successfully.", userInfo.email());
                return ResponseEntity.ok("Investor Added Successfully");
            } catch (DateTimeParseException e) {
                log.error("Invalid date format for dateOfBirth: {}", userInfo.dateOfBirth(), e);
                return ResponseEntity.badRequest().body("Invalid date format for dateOfBirth.");
            }
        }
    }
}
