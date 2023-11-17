package com.example.aiwebservice.config;

import com.example.aiwebservice.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   //@PreAuthorize를 메소드 단위로 추가하기 위해 적용(유저 권한에 따라 접근 가능한 메소드를 제한할 때 사용)
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    public SecurityConfig(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                // Spring Security should completely ignore URLs starting with /resources/
                .requestMatchers("/resources/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 토큰을 사용하기 때문에 csrf 설정 disable
                .csrf((csrf) -> csrf.disable())

                // 예외 처리 시 직접 만들었던 클래스 추가

                // 세션 사용하지 않기 때문에 세션 설정 STATELESS
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 토큰이 없는 상태에서 요청이 들어오는 API들은 permitAll
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.requestMatchers("/login").
                        permitAll().anyRequest().authenticated())

                // JwtFilter를 addFilterBefore로 등록했던 jwtSecurityConfig 클래스 적용
                .apply(new JwtSecurityConfig(tokenProvider));

        return http.build();
    }
}