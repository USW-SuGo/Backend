package com.usw.sugo.global.exception;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {
    NOT_ALLOWED(BAD_REQUEST, "해당 요청을 수행할 권한이 없습니다."),
    USER_BAD_REQUEST(BAD_REQUEST, "파라미터가 올바르지 않습니다."),
    CATEGORY_NOT_FOUND(BAD_REQUEST, "해당 카테고리 이름을 찾을 수 없습니다."),
    EMAIL_NOT_VALIDATED(BAD_REQUEST, "이메일 형식이 올바르지 않습니다. 교내 웹 메일 주소만 입력할 수 있습니다."),
    POST_NOT_FOUND(BAD_REQUEST, "해당 게시글은 찾을 수 없습니다."),
    USER_NOT_SEND_AUTH_EMAIL(BAD_REQUEST, "이메일 인증을 발송하지 않은 사용자입니다."),
    PAYLOAD_NOT_VALID(BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    NOTE_NOT_FOUNDED(BAD_REQUEST, "해당 쪽지방을 찾을 수 없습니다."),
    NOTE_ALREADY_CREATED(BAD_REQUEST, "이미 생성된 쪽지방이 존재합니다."),
    DO_NOT_CREATE_YOURSELF(BAD_REQUEST, "자신에게 쪽지방을 개설할 수 없습니다."),
    DO_NOT_LIKE_YOURSELF(BAD_REQUEST, "자신의 게시물은 좋아요를 기록할 수 없습니다."),
    USER_UNAUTHORIZED(UNAUTHORIZED, "권한이 없습니다."),
    PARAM_VALID_ERROR(BAD_REQUEST, "파라미터가 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "올바른 메서드 요청이 아닙니다."),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 내부 에러입니다. 관리자에게 문의하세요."),
    DUPLICATED_EMAIL(BAD_REQUEST, "이미 존재하는 이메일 입니다."),
    DUPLICATED_LOGINID(BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    IS_SAME_PASSWORD(BAD_REQUEST, "변경할 비밀번호가 이전과 같습니다."),
    INVALID_DEPARTMENT(BAD_REQUEST, "존재하지 않는 학과입니다."),
    USER_NOT_EXIST(BAD_REQUEST, "존재하지 않는 회원 정보입니다."),
    PASSWORD_NOT_CORRECT(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    ALREADY_EVALUATION(BAD_REQUEST, "매너 평가는 하루에 한 번만 수행할 수 있습니다."),
    ALREADY_UP_POSTING(BAD_REQUEST, "게시글 갱신은 하루에 한 번만 수행할 수 있습니다."),
    JWT_MALFORMED_EXCEPTION(BAD_REQUEST, "JWT_MALFORMED_EXCEPTION"),
    JWT_EXPIRED_EXCEPTION(FORBIDDEN, "토큰이 만료되었습니다."),
    REQUIRE_TOKEN(UNAUTHORIZED, "해당 요청은 토큰이 필요합니다."),
    INTERNAL_UPLOAD_EXCEPTION(INTERNAL_SERVER_ERROR, "이미지 업로드 중 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
