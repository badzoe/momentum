package com.MomentumInvestments.MomentumInvestmentsApplication.config.security;


import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.CredentialsCreation;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Credentials;
import com.MomentumInvestments.MomentumInvestmentsApplication.repository.CredentialsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserInfoService implements UserDetailsService {
    private final CredentialsRepository repository;
    private final PasswordEncoder encoder;
    public UserInfoService(CredentialsRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Credentials> userDetail = repository.findByName(username);
        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }
    public String addUser(CredentialsCreation userInfo) {
        Credentials newCredentials = new Credentials ();
        newCredentials.setId (0);
        newCredentials.setName(userInfo.name());
        newCredentials.setEmail(userInfo.email());
        newCredentials.setPassword(encoder.encode(userInfo.password()));
        newCredentials.setRoles("user");
        repository.save(newCredentials);
        return "User Added Successfully";
    }
}
