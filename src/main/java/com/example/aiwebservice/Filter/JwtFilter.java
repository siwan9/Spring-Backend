package com.example.aiwebservice.Filter;

import com.example.aiwebservice.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request); // http 요청으로부터 토큰을 만든다.

        //Jwt에 string이 있고, claim을 잘 파싱할 수 있는 토큰이면 security context에 저장 진행
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            Authentication authentication = tokenProvider.getAuthentication(jwt);
        }
        else{
            logger.debug("유효한 토큰이 없습니다");
        }
    }
    public String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}