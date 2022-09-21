package com.usw.sugo.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {

    JWT_MALFORMED_EXCEPTION(BAD_REQUEST, "JWT_MALFORMED_EXCEPTION"),
    JWT_EXPIRED_EXCEPTION(FORBIDDEN, "토큰이 만료되었습니다."),
    JWT_UNSUPPORTED_EXCEPTION(BAD_REQUEST, "JWT_UNSUPPORTED_EXCEPTION"),
    JWT_IllegalARGUMENT_EXCEPTION(BAD_REQUEST, "JWT_IllegalARGUMENT_EXCEPTION"),
    REQUIRE_TOKEN(BAD_REQUEST, "해당 요청은 토큰이 필요합니다."),


    ;

    private final HttpStatus httpStatus;
    private final String message;

}
