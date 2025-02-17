package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record InvestorAuthenticationRequest(
        @NotNull(message = "Email cannot be null")
        @Email(message = "Email should be valid")
        String username,
        String password) {

        public String getEmail() {
                return username;

        }// No need for a getter for email; it is automatically created.
}
