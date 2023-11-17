package com.example.aiwebservice.config;

import com.example.aiwebservice.Filter.JwtFilter;
import com.example.aiwebservice.jwt.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;    //WebSecurityConfigurerAdapter 상속 클래스
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;
    public JwtSecurityConfig(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {

        System.out.println("configure");

        JwtFilter customFilter = new JwtFilter(tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
