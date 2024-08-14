package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request;

import java.time.LocalDate;

public record InvestorCreation(String name, String surname, LocalDate dateOfBirth, String address, String phoneNumber, String email, String password) {

}