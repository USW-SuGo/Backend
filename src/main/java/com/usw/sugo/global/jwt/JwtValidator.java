package com.usw.sugo.global.jwt;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.TokenErrorCode;
import com.usw.sugo.exception.UserErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.security.Key;

import static com.usw.sugo.exception.TokenErrorCode.JWT_EXPIRED_EXCEPTION;

@Component
public class JwtValidator {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Jwt 유효성 검사
    public void validateToken(String token) throws ExpiredJwtException {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
        }
        catch (MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            throw new BadCredentialsException("INVALID", ex);
        }
        catch (ExpiredJwtException exception) {
            throw new CustomException(JWT_EXPIRED_EXCEPTION);
        }
    }

}
