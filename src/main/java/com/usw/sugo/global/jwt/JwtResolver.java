package com.usw.sugo.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtResolver {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // AccessToken 에서 Claims 꺼내기
    public Claims jwtResolve(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    // AccessToken 에서 userId 꺼내기
    public Long jwtResolveToUserId(String token) {
        Object claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody().get("id");

        return Long.valueOf(String.valueOf(claims));
    }

    // AccessToken 에서 userId 꺼내기
    public String jwtResolveToUserEmail(String token) {
        Object claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody().get("email");

        return String.valueOf(claims);
    }

    // AccessToken 에서 loginId 꺼내기
    public String jwtResolveToUserLoginId(String token) {
        Object claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody().get("loginId");

        return String.valueOf(claims);
    }

    // AccessToken 에서 Nickname 꺼내기
    public String jwtResolveToUserNickname(String token) {
        Object claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody().get("nickname");
        return String.valueOf(claims);
    }

    // AccessToken 에서 Status 꺼내기
    public String jwtResolveToUserStatus(String token) {
        Object claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token).getBody().get("status");
        return (String) claims;
    }
}
