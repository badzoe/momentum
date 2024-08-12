package com.MomentumInvestments.MomentumInvestmentsApplication.controller;

import com.MomentumInvestments.MomentumInvestmentsApplication.config.security.JwtService;
import com.MomentumInvestments.MomentumInvestmentsApplication.config.security.UserInfoService;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.AuthenticationRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.CredentialsCreation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@Slf4j
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final UserInfoService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    @PostMapping(path = "/login")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody final AuthenticationRequest authRequest) {

        log.info("Muno");

        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(),encoder.encode(authRequest.password()) ));
            log.info("Muno 2");
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.username());
                return ResponseEntity.ok(token);
            } else {
                throw new UsernameNotFoundException("Invalid user request!");
            }}catch (Exception ex){
            log.info(ex.toString());
            return null;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> addNewUser(@RequestBody CredentialsCreation userInfo) {
        String response = service.addUser(userInfo);
        log.info("Testing");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
