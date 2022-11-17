package com.usw.sugo.global.jwt;

import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserDetailsRepository;
import com.usw.sugo.domain.refreshtoken.entity.RefreshToken;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtGenerator {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtResolver jwtResolver;
    private final UserDetailsRepository userRepository;

    // 어느시점에 secretKey 값이 등록되는가?
    @Value("${spring.jwt.secret-key}")
    private String secretKey;

//    private final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
//    private final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일

//     테스트 환경 JWT 만료기간 1 시작
//    private final long ACCESS_TOKEN_EXPIRE_TIME = 1 * 60 * 1000L; // 1분
//    private final long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분
//     테스트 환경 JWT 만료기간 1 종료

    // 테스트 환경 JWT 만료기간
    private final long ACCESS_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일
    private final long REFRESH_TOKEN_EXPIRE_TIME = 15 * 24 * 60 * 60 * 1000L; // 15일

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
        claims.put("loginId", user.getLoginId());
        claims.put("nickname", user.getNickname());
        claims.put("email", user.getEmail());
        claims.put("status", user.getStatus().toString());

        // Bearer Access Token 생성
        return "Bearer " + Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(accessTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 필터 내에서 AccessToken 생성
    public String createAccessTokenInFilter(Long id, String loginId, String nickname, String email, String status) {
        Date now = new Date();
        Date accessTokenExpireIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role, restricted)
        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");
        claims.put("id", id);
        claims.put("loginId", loginId);
        claims.put("nickname", nickname);
        claims.put("email", email);
        claims.put("status", status);

        // Bearer Access Token 생성
        return "Bearer " + Jwts.builder()
                .setHeaderParam("type", "JWT")
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
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(refreshTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        RefreshToken entityFormRefreshToken = RefreshToken.builder()
                .user(user)
                .payload(stringRefreshToken)
                .build();

        refreshTokenRepository.save(entityFormRefreshToken);

        return "Bearer " + stringRefreshToken;
    }

    // RefreshToken 신규 생성
    @Transactional
    public String createRefreshTokenInFilter(Long id) {
        Date now = new Date();
        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        //페이로드에 남길 정보들 (Id, loginId, role, restricted)
        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        // Access Token 생성
        String stringRefreshToken = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(refreshTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        User user = userRepository.findById(id).get();

        RefreshToken entityFormRefreshToken = RefreshToken.builder()
                .user(user)
                .payload(stringRefreshToken)
                .build();

        refreshTokenRepository.save(entityFormRefreshToken);

        return "Bearer " + stringRefreshToken;
    }

    // RefershToken 갱신
    @Transactional
    public String updateRefreshToken(User user) {
        Date now = new Date();
        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        // RefreshToken Token 생성
        String stringRefreshToken = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setClaims(claims)
                .setExpiration(refreshTokenExpireIn)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        refreshTokenRepository.refreshPayload(user.getId(), stringRefreshToken);

        return "Bearer " + stringRefreshToken;
    }
}
