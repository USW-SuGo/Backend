package com.usw.sugo.global.jwt;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.refreshtoken.RefreshToken;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.UserErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtGenerator {

    private final RefreshTokenRepository refreshTokenRepository;

    // 어느시점에 secretKey 값이 등록되는가?
    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //AccessToken 생성
    public String createAccessToken(User user) {
        Date now = new Date();
        Date accessTokenExpireIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role, restricted)
        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");
        claims.put("id", user.getId());
        claims.put("nickname", user.getNickname());
        claims.put("email", user.getEmail());
        claims.put("status", user.getStatus());

        // Bearer Access Token 생성
        return "Bearer " + Jwts.builder()
                .setHeaderParam("type","JWT")
                .setClaims(claims)
                .setExpiration(accessTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // RefreshToken 신규 생성
    @Transactional
    public String createRefreshToken(User user) {
        Date now = new Date();
        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role, restricted)
        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        // Access Token 생성
        String stringRefreshToken = Jwts.builder()
                .setHeaderParam("type","JWT")
                .setClaims(claims)
                .setExpiration(refreshTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        RefreshToken domainRefreshToken = RefreshToken.builder()
                .userId(user.getId())
                .payload(stringRefreshToken)
                .build();

        refreshTokenRepository.save(domainRefreshToken);

        return "Bearer " + stringRefreshToken;
    }
    //AccessToken 생성
    @Transactional
    public String refreshRefreshToken(User user) {
        Date now = new Date();
        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role, restricted)
        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        // Access Token 생성
        String stringRefreshToken =  "Bearer " + Jwts.builder()
                .setHeaderParam("type","JWT")
                .setClaims(claims)
                .setExpiration(refreshTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        RefreshToken domainRefreshToken = RefreshToken.builder()
                .userId(user.getId())
                .payload(stringRefreshToken)
                .build();

        refreshTokenRepository.refreshPayload(user.getId(), stringRefreshToken);

        return stringRefreshToken;
    }
}
