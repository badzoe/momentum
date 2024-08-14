package com.MomentumInvestments.MomentumInvestmentsApplication.config.security;


import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

public class UserInfoDetails implements UserDetails {

    private String name;
    private String password;
    private List<GrantedAuthority> authorities;
    public UserInfoDetails(Investor investor) {
        name = investor.getEmail();
        password = investor.getPassword();
        authorities = Arrays.stream(investor.getRole().split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_"+ role.trim()))
                //.map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return name;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}