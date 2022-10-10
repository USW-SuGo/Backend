package com.usw.sugo.global.exception;


import lombok.*;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_BAD_REQUEST(BAD_REQUEST, "파라미터가 올바르지 않습니다."),
    CHATTING_ROOM_NOT_FOUND(BAD_REQUEST, "해당 채팅방은 찾을 수 없습니다."),
    POST_NOT_FOUND(BAD_REQUEST, "해당 게시글은 찾을 수 없습니다."),


    USER_UNAUTHORIZED(UNAUTHORIZED, "권한이 없습니다."),
    PARAM_VALID_ERROR(BAD_REQUEST, "파라미터가 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "올바른 메서드 요청이 아닙니다." ),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 내부 에러입니다. 관리자에게 문의하세요." ),

    DUPLICATED_EMAIL(BAD_REQUEST, "이미 인증메일이 발송된 이메일 입니다. 메일 수신함을 확인해주세요."),
    DUPLICATED_LOGINID(BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    NOT_AUTHORIZED_EMAIL(BAD_REQUEST, "이메일 인증을 수행하지 않은 사용자 입니다. 메일 수신함을 확인해주세요."),
    INVALID_AUTH_TOKEN(BAD_REQUEST, "인증 토큰이 올바르지 않습니다. 관리자에게 문의해주세요"),
    IS_SAME_PASSWORD(BAD_REQUEST, "변경할 비밀번호가 이전과 같습니다."),
    INVALID_PARAMETER(BAD_REQUEST, "처리할 수 없는 파라미터 내용이 있습니다."),
    INVALID_DEPARTMENT(BAD_REQUEST, "존재하지 않는 학과입니다."),
    USER_NOT_EXIST(BAD_REQUEST, "존재하지 않는 회원 정보입니다."),
    PASSWORD_NOT_CORRECT(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_JOIN(BAD_REQUEST, "이미 회원가입이 되어있는 유저입니다."),

    ALREADY_EVALUATION(BAD_REQUEST, "매너 평가는 하루에 한 번만 수행할 수 있습니다."),
    ALREADY_UP_POSTING(BAD_REQUEST, "게시글 갱신은 하루에 한 번만 수행할 수 있습니다."),


    JWT_MALFORMED_EXCEPTION(BAD_REQUEST, "JWT_MALFORMED_EXCEPTION"),
    JWT_EXPIRED_EXCEPTION(FORBIDDEN, "토큰이 만료되었습니다."),
    JWT_UNSUPPORTED_EXCEPTION(BAD_REQUEST, "JWT_UNSUPPORTED_EXCEPTION"),
    JWT_IllegalARGUMENT_EXCEPTION(BAD_REQUEST, "JWT_IllegalARGUMENT_EXCEPTION"),
    REQUIRE_TOKEN(BAD_REQUEST, "해당 요청은 토큰이 필요합니다.");


    private final HttpStatus status;
    private final String message;
}
