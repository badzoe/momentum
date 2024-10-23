package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorAuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.services.InvestorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.MomentumInvestments.MomentumInvestmentsApplication.constants.ApiURL.REGISTER_INVESTOR;
import static com.MomentumInvestments.MomentumInvestmentsApplication.constants.ApiURL.SAVE_INVESTOR;
import static com.MomentumInvestments.MomentumInvestmentsApplication.constants.Constant.INV;
import static com.MomentumInvestments.MomentumInvestmentsApplication.constants.Constant.MESSAGE;

@Controller
@AllArgsConstructor
@Slf4j
@Tag(name = "Investor")
public class InvestorController {

    private final InvestorService investorService;
    private static final Logger logger = LoggerFactory.getLogger(InvestorController.class);

    @GetMapping("/")
    public String showHomePage(Model model) {
        // Any additional logic can be added here before returning the view
        return "HomePage"; // This corresponds to your Thymeleaf template file (index.html)
    }

    @GetMapping("/register")
    public String index(Model model) {
        model.addAttribute(INV, new Investor());
        return "RegisterInvestor";
    }

    @GetMapping("/registration-success")
    public String showRegistrationSuccessPage(Model model) {
        return "RegistrationSuccessful"; // This refers to the Thymeleaf template file registrationSuccess.html
    }

    @GetMapping("/investors")
    @Operation(summary = "Get All Investor")
    public ResponseEntity<List<Investor>> getAllInvestors() {
        logger.info("Fetching all investors");
        List<Investor> investors = investorService.getAllInvestors();
        logger.debug("Number of investors fetched: {}", investors.size());
        return ResponseEntity.ok(investors);
    }

    @GetMapping("/{investorID:[0-9]+}")
    @Operation(summary = "Get Investor By ID")
    public ResponseEntity<Investor> getInvestorByID(@PathVariable Long investorID) {
        logger.info("Fetching investor with ID: {}", investorID);
        Investor investor = investorService.getInvestorByID(investorID);
        return ResponseEntity.ok(investor);
    }

    @GetMapping("investorBy/{productType}")
    @Operation(summary = "Get Investors By Product Type")
    public ResponseEntity<List<Investor>> getInvestorsByProductType(@PathVariable String productType) {
        logger.info("Fetching investors for product type: {}", productType);
        List<Investor> investors = investorService.getInvestorsByProductType(productType);
        logger.info("Retrieved {} investors for product type: {}", investors.size(), productType);
        logger.debug("Investors details: {}", investors);
        return ResponseEntity.ok(investors);
    }
    @GetMapping("/login")
    public String login(Model model) {
        return "InvestorLogin"; // Return the name of your login page view
    }


    @PostMapping(path = "/saveLogin")
    @Operation(summary = "Login & Get JWT Token used to access all tokens")
    public String authenticateAndGetToken(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        logger.info("Authentication attempt for email: {}", email);
        InvestorAuthenticationRequest authRequest = new InvestorAuthenticationRequest(email, password);
        ResponseEntity<String> response = investorService.authenticateInvestor(authRequest);

        if (response.getStatusCode().is2xxSuccessful()) {
            logger.info("Authentication successful for email: {}", email);
            // Redirect to withdrawal page on successful login
            return "redirect:/withdraw";
        } else {
            logger.warn("Authentication failed for email: {}", email);
            // Add error message and return to login page
            model.addAttribute("errorMessage", "Authentication failed. Please try again.");
            return "login"; // The name of your Thymeleaf login page template
        }
    }

    @GetMapping(REGISTER_INVESTOR)
    public String newInvestors(@ModelAttribute(MESSAGE) String message, Model model) {
        model.addAttribute(INV, new Investor());
        model.addAttribute(MESSAGE, message);
        return "RegisterInvestor";
    }

    @PostMapping(SAVE_INVESTOR)
    public String addNewUser(@Valid @ModelAttribute InvestorCreation investor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(MESSAGE, "Validation errors occurred");
            return "RegisterInvestor";
        }

        ResponseEntity<String> response = investorService.createInvestor(investor);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute(MESSAGE, "Save successful");
            return "redirect:/registration-success"; // Ensure this path is correct
        } else {
            model.addAttribute(MESSAGE, "Save failed");
            return "RegisterInvestor";
        }
    }
}
