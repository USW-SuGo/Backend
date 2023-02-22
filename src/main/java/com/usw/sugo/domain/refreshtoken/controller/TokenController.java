package com.usw.sugo.domain.refreshtoken.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.refreshtoken.service.RefreshTokenService;
import com.usw.sugo.global.apiresult.ApiResultFactory;
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

    private final RefreshTokenService refreshTokenService;

    @ResponseStatus(OK)
    @PostMapping
    public ResponseEntity<Object> updateToken(
        @RequestHeader String authorization
    ) {

        String requestRefreshToken = authorization.substring(7);
        Map<String, String> result =
            refreshTokenService.executeReIssueToken(requestRefreshToken);

        HttpHeaders response = new HttpHeaders();
        response.set("Authorization", result.toString());

        return ResponseEntity
            .status(OK)
            .headers(response)
            .body(ApiResultFactory.getSuccessFlag());
    }
}