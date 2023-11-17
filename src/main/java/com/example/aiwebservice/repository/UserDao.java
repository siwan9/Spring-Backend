package com.example.aiwebservice.repository;

import com.example.aiwebservice.dto.CustomUserDetails;
import org.springframework.jdbc.core.JdbcTemplate;

public interface UserDao {
    public CustomUserDetails getUserInfo(String username);
}
