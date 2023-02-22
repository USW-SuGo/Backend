package com.usw.sugo.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class JwtResolver {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private final UserDetailsService userDetailsService;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 공용 해석
    public Claims commonResolve(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token).getBody();
    }

    // 갱신이 필요하면 True를 반환한다.
    public boolean isNeedToUpdateRefreshToken(String refreshToken) {
        Date claims = commonResolve(refreshToken).getExpiration();
        LocalDateTime localDateTimeClaims = claims.toInstant().atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        LocalDateTime subDetractedDateTime = LocalDateTime.now().plusSeconds(604800);
        return localDateTimeClaims.isBefore(subDetractedDateTime);
    }

    public Long jwtResolveToUserId(String token) {
        Object claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token).getBody().get("id");

        return Long.valueOf(String.valueOf(claims));
    }

    public Authentication getAuthentication(String loginId) {
        UserDetails user = userDetailsService.loadUserByUsername(loginId);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

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
