package com.usw.sugo.global.jwt;

import com.usw.sugo.domain.refreshtoken.RefreshToken;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.domain.user.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JwtGenerator {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtResolver jwtResolver;
    private final JwtValidator jwtValidator;

    // 어느시점에 secretKey 값이 등록되는가?
    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    //배포 환경 JWT 만료 기간
//    private final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L; // 30분
//    private final long REFRESH_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일

    // 테스트 환경 JWT 만료기간
//    private final long ACCESS_TOKEN_EXPIRE_TIME = 1 * 60 * 1000L; // 1분
//    private final long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분

//    // 테스트 환경 JWT 만료기간 2
    private final long ACCESS_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일
    private final long REFRESH_TOKEN_EXPIRE_TIME = 15 * 24 * 60 * 60 * 1000L; // 15일

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date accessTokenExpireIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");
        claims.put("id", user.getId());
        claims.put("loginId", user.getLoginId());
        claims.put("nickname", user.getNickname());
        claims.put("email", user.getEmail());
        claims.put("status", user.getStatus());

        // Bearer Access Token 생성
        return "Bearer " + Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setClaims(claims)
            .setExpiration(accessTokenExpireIn)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public String generateRefreshToken(User user) {
        Optional<RefreshToken> findRefreshTokenByUserId = refreshTokenRepository.findByUserId(
            user.getId());

        if (findRefreshTokenByUserId.isPresent()) {
            String refreshToken = refreshTokenRepository.findByUserId(user.getId()).get()
                .getPayload();

            if (jwtValidator.refreshTokenIsExpired(refreshToken)) {
                return updateRefreshToken(user);
            }
            else if (!jwtValidator.refreshTokenIsExpired(refreshToken)
                && jwtResolver.isNeedToUpdateRefreshToken(refreshToken)) {
                return updateRefreshToken(user);
            }
            else if (!jwtValidator.refreshTokenIsExpired(refreshToken)
                && !jwtResolver.isNeedToUpdateRefreshToken(refreshToken)) {
                return "Bearer " + refreshToken;
            }
        }
        return createRefreshToken(user);
    }

    @Transactional
    public String createRefreshToken(User user) {
        Date now = new Date();
        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

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

    public String updateRefreshToken(User user) {
        Date now = new Date();
        Date refreshTokenExpireIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        // RefreshToken Token 생성
        String updatedRefreshToken = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setClaims(claims)
            .setExpiration(refreshTokenExpireIn)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();

        refreshTokenRepository.refreshPayload(user.getId(), updatedRefreshToken);
        return "Bearer " + updatedRefreshToken;
    }

    public Map<String, String> wrapTokenPair(String accessToken, String refreshToken) {
        return new HashMap<>() {{
            put("AccessToken", (accessToken));
            put("RefreshToken", (refreshToken));
        }};
    }
}
