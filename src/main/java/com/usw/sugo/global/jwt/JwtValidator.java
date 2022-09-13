package com.usw.sugo.global.jwt;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.UserErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtValidator {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Jwt 유효성 검사
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build();
        }
        // 400 Error - MalFormed
        catch (MalformedJwtException ex) {
            throw new CustomException(UserErrorCode.JWT_MALFORMED_EXCEPTION);
        }
        // 401 Error - Expired
        catch (ExpiredJwtException ex) {
            throw new CustomException(UserErrorCode.JWT_EXPIRED_EXCEPTION);
        }
        // 400 Error - UnSupported
        catch (UnsupportedJwtException ex) {
            throw new CustomException(UserErrorCode.JWT_UNSUPPORTED_EXCEPTION);
        }
        // 400 Error - Illegal
        catch (IllegalArgumentException ex) {
            throw new CustomException(UserErrorCode.JWT_IllegalARGUMENT_EXCEPTION);
        }
    }
}
