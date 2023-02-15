package com.usw.sugo.domain.refreshtoken.controller;

import static com.usw.sugo.global.exception.ExceptionType.JWT_MALFORMED_EXCEPTION;
import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.refreshtoken.RefreshToken;
import com.usw.sugo.domain.refreshtoken.service.RefreshTokenService;
import com.usw.sugo.global.apiresult.ApiResultFactory;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtValidator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/token")
@RestController
public class TokenController {

    private final JwtValidator jwtValidator;
    private final RefreshTokenService refreshTokenService;

    @ResponseStatus(OK)
    @PostMapping
    public ResponseEntity<Object> updateToken(@RequestHeader String authorization) {
        String requestRefreshToken = authorization.substring(7);
        if (jwtValidator.validateToken(requestRefreshToken)) {
            RefreshToken refreshToken = refreshTokenService.loadRefreshTokenByPayload(
                requestRefreshToken);
            Map<String, String> result = refreshTokenService.reIssueToken(refreshToken);
            HttpHeaders response = new HttpHeaders();
            response.set("Authorization", result.toString());
            return ResponseEntity
                .status(OK)
                .headers(response)
                .body(ApiResultFactory.getSuccessFlag());
        }
        throw new CustomException(JWT_MALFORMED_EXCEPTION);
    }
}
