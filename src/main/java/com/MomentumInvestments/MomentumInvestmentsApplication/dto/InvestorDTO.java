package com.MomentumInvestments.MomentumInvestmentsApplication.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class InvestorDTO {

    private long id;
    private String name;
    private String surname;
    private String dateOfBirth;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private String role;
}
