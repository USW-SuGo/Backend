package com.usw.sugo.domain.refreshtoken;

import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.exception.CustomException;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static com.usw.sugo.exception.ErrorCode.JWT_MALFORMED_EXCEPTION;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RequestMapping("/token")
@RestController
public class TokenController {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtGenerator jwtGenerator;
    private final JwtValidator jwtValidator;

    // 토큰 갱신 (RefreshToken 을 매개변수로 받는다.)
    @PostMapping
    public ResponseEntity<Object> updateToken(@RequestHeader String authorization) {

        String requestRefreshToken = authorization.substring(7);

        // RefreshToken 검증이 끝나면, 토큰 재발급
        if (jwtValidator.validateToken(requestRefreshToken) &&
                refreshTokenRepository.findByPayload(requestRefreshToken).isPresent()) {

            RefreshToken requestRefreshTokenDomain = refreshTokenRepository.findByPayload(requestRefreshToken).get();

            User requestUser = requestRefreshTokenDomain.getUser();

            String accessToken = jwtGenerator.createAccessToken(requestUser);
            String refreshToken = jwtGenerator.updateRefreshToken(requestUser);

            return ResponseEntity.status(OK).body(new HashMap<>() {{
                put("AccessToken", accessToken);
                put("RefreshToken", refreshToken);
            }});
        }
        // 해당 리프레시 토큰이 DB에 없으면 에러
        throw new CustomException(JWT_MALFORMED_EXCEPTION);
    }
}
