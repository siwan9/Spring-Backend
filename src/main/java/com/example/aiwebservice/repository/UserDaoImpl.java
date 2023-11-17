package com.example.aiwebservice.repository;

import com.example.aiwebservice.dto.CustomUserDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao{
    private final JdbcTemplate jdbctemplate;

    public UserDaoImpl(JdbcTemplate jdbctemplate){
        this.jdbctemplate = jdbctemplate;
    }

    public CustomUserDetails getUserInfo(String username){
        String sql = "select * from member where memberEmail = ?";
        List<CustomUserDetails> userDetails = jdbctemplate.query(sql, new UserMapper(), username);
        return userDetails.stream().findAny().get();
    }
    class UserMapper implements RowMapper<CustomUserDetails> {
        @Override
        public CustomUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setMemberEmail(rs.getString("memberEmail"));
            customUserDetails.setMemberPassword(rs.getString("memberPassword"));
            customUserDetails.setName(rs.getString("name"));
            return customUserDetails;
        }
    }
}