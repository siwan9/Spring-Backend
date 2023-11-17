package com.example.aiwebservice.service;

import com.example.aiwebservice.dto.CustomUserDetails;
import com.example.aiwebservice.repository.UserDaoImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("UserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private UserDaoImpl userDaoImpl;

    public CustomUserDetailsService(UserDaoImpl userDaoImpl){
        this.userDaoImpl = userDaoImpl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails userDetails = userDaoImpl.getUserInfo(username);
        if(userDetails==null) {
            throw new UsernameNotFoundException(username);
        }
        return userDetails;
    }
}
