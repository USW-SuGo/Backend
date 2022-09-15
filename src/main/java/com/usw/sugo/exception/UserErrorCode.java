package com.usw.sugo.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    DUPLICATED_EMAIL(BAD_REQUEST, "이미 인증메일이 발송된 이메일 입니다. 메일 수신함을 확인해주세요."),
    NOT_AUTHORIZED_EMAIL(BAD_REQUEST, "이메일 인증을 수행하지 않은 사용자 입니다. 메일 수신함을 확인해주세요."),
    INVALID_AUTH_TOKEN(BAD_REQUEST, "인증 토큰이 올바르지 않습니다. 관리자에게 문의해주세요"),
    IS_SAME_PASSWORD(BAD_REQUEST, "변경할 비밀번호가 이전과 같습니다."),
    INVALID_PARAMETER(BAD_REQUEST, "처리할 수 없는 파라미터 내용이 있습니다."),
    INVALID_DEPARTMENT(BAD_REQUEST, "존재하지 않는 학과입니다."),
    USER_NOT_EXIST(BAD_REQUEST, "존재하지 않는 회원 정보입니다."),
    PASSWORD_NOT_CORRECT(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    JWT_MALFORMED_EXCEPTION(BAD_REQUEST, "JWT_MALFORMED_EXCEPTION"),
    JWT_EXPIRED_EXCEPTION(BAD_REQUEST, "JWT_EXPIRED_EXCEPTION"),
    JWT_UNSUPPORTED_EXCEPTION(BAD_REQUEST, "JWT_UNSUPPORTED_EXCEPTION"),
    JWT_IllegalARGUMENT_EXCEPTION(BAD_REQUEST, "JWT_IllegalARGUMENT_EXCEPTION"),

    ;

    private final HttpStatus httpStatus;
    private final String message;

}
