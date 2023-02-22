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
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1 * 60 * 1000L; // 1분
    private final long REFRESH_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L; // 5분

    //    // 테스트 환경 JWT 만료기간 2
//    private final long ACCESS_TOKEN_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000L; // 14일
//    private final long REFRESH_TOKEN_EXPIRE_TIME = 15 * 24 * 60 * 60 * 1000L; // 15일

    private Key getSigningKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 엑세스 토큰 발급
    public String generateAccessToken(User user) {
        final Date accessTokenExpiredIn = new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        final Claims claims = Jwts.claims();
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
            .setExpiration(accessTokenExpiredIn)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    @Transactional
    public String generateRefreshToken(User user) {
        final Optional<RefreshToken> findRefreshTokenByUserId =
            refreshTokenRepository.findByUserId(user.getId());

        // 이미 로그인해서 토큰을 발급 받은 적이 있는 유저일 때
        if (findRefreshTokenByUserId.isPresent()) {
            String refreshToken =
                refreshTokenRepository.findByUserId(user.getId()).get().getPayload();
            // 리프레시 토큰이 만료되었으면
            if (jwtValidator.refreshTokenIsExpired(refreshToken)) {
                return updateRefreshToken(user);
            }
            // 리프레시 토큰이 만료되진 않았으나 갱신이 필요할 때
            else if (!jwtValidator.refreshTokenIsExpired(refreshToken)
                && jwtResolver.isNeedToUpdateRefreshToken(refreshToken)) {
                return updateRefreshToken(user);
            }
            // 리프레시 토큰이 만료되지 않았고 갱신 또한 필요하지 않을 때
            else if (!jwtValidator.refreshTokenIsExpired(refreshToken)
                && !jwtResolver.isNeedToUpdateRefreshToken(refreshToken)) {
                return "Bearer " + refreshToken;
            }
        }
        return createNewRefreshToken(user);
    }

    @Transactional
    public String createNewRefreshToken(User user) {
        final Date refreshTokenExpiredIn = new Date(
            new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        final Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        final String stringRefreshToken = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setClaims(claims)
            .setExpiration(refreshTokenExpiredIn)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();

        RefreshToken entityFormRefreshToken = RefreshToken.builder()
            .user(user)
            .payload(stringRefreshToken)
            .build();

        refreshTokenRepository.save(entityFormRefreshToken);
        return "Bearer " + stringRefreshToken;
    }

    @Transactional
    public String updateRefreshToken(User user) {
        final Date refreshTokenExpiredIn = new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        final Claims claims = Jwts.claims();
        claims.setSubject("USW-SUGO-BY-KDH");

        // RefreshToken Token 생성
        final String updatedRefreshToken = Jwts.builder()
            .setHeaderParam("type", "JWT")
            .setClaims(claims)
            .setExpiration(refreshTokenExpiredIn)
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
