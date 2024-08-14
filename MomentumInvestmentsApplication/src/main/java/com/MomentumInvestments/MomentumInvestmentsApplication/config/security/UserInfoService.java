package com.MomentumInvestments.MomentumInvestmentsApplication.config.security;


import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.InvestorCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.InvestorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class UserInfoService implements UserDetailsService {
    private final InvestorRepository investorRepository;
    private final PasswordEncoder encoder;
    public UserInfoService(InvestorRepository investorRepository, PasswordEncoder encoder) {
        this.investorRepository = investorRepository;
        this.encoder = encoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Investor> userDetail = investorRepository.findByName(username);
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }
}