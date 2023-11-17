package com.example.aiwebservice.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
    private int userId;
    private String memberEmail;
    private String memberPassword;
    private String name;
    private Collection<? extends GrantedAuthority> auth;

    public CustomUserDetails(String memberEmail, String memberPassword, Collection<? extends GrantedAuthority> authorities){
        this.memberEmail = memberEmail;
        this.memberPassword = memberPassword;
        this.auth = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.auth;
    }

    @Override
    public String getPassword() {
        return memberPassword;
    }

    @Override
    public String getUsername() {
        return memberEmail;
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

    public void setMemberEmail(String memberEmail){this.memberEmail = memberEmail;}
    public void setMemberPassword(String memberPassword){this.memberPassword = memberPassword;}
    public void setName(String name){this.name = name;}
}
