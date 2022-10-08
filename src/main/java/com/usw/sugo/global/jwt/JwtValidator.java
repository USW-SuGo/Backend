package com.usw.sugo.global.jwt;

import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.NoSuchElementException;

import static com.usw.sugo.global.exception.ErrorCode.JWT_EXPIRED_EXCEPTION;

@Component
public class JwtValidator {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Jwt 유효성 검사
    public boolean validateToken(String token) throws ExpiredJwtException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
        }
        catch (NoSuchElementException | BadCredentialsException |
               MalformedJwtException | IllegalArgumentException |
               SignatureException ex) {
            throw new CustomException(ErrorCode.JWT_MALFORMED_EXCEPTION);
        }
        catch (ExpiredJwtException exception) {
            throw new CustomException(JWT_EXPIRED_EXCEPTION);
        }

        return true;
    }

}
