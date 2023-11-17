package com.example.aiwebservice.jwt;

import com.example.aiwebservice.dto.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;
    public TokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds){
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰을 만들어 클라이언트에게 리턴해주기 위한 메소드
    public String createToken(Authentication authentication){

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }
    public Authentication getAuthentication(String token){
        // getBody : claims 객체로 반환
        // claims 객체에서 AUTHORITIES_KEY에 해당하는 것을 가져온 후 문자열화하고, ,단위로 나눠서 만든 배열을 스트림화 시킴
        // 배열 요소를 SimpleGrantedAuthority화 시키기 위해 람다식 사용
        // list로 반환
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // claim의 subject : 토큰의 제목
        CustomUserDetails principal = new CustomUserDetails(claims.getSubject(), "", authorities);


        // isAuthentication이 true인 토큰을 발급받기 위해서 인자가 3개인 메소드를 호출해야한다.
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    public boolean validateToken(String token){ //토큰을 파싱하여 claim을 잘 얻을 수 있는가?
        try{
            // parserBuilder -> parser -> String(주어진 토큰을 파싱하여 잘 파싱되면 payload를 스트링으로 리턴)
            // parseClaimsJws : payload를 키로 서명했을 때 토큰에 적혀진 서명과 일치하는지 확인
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);  //parseClaimsJws 토큰 유효성 검사 및 jwt 서명, 클레인 정보 확인
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) { log.info("잘못된 JWT 토큰 서명"); }
        catch (ExpiredJwtException e) { log.info("만료된 JWT 토큰"); }
        catch (UnsupportedJwtException e) { log.info("지원되지 않는 JWT 토큰"); }
        catch (IllegalArgumentException e) { log.info("잘못된 JWT 토큰"); }
        return false;
    }
}