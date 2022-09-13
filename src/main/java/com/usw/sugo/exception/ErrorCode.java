package com.usw.sugo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST */
    INVALID_DEPARTMENT(BAD_REQUEST, "존재하는 학과 명이 아닙니다."),
    IS_SAME_PASSWORD(BAD_REQUEST, "이전 비밀번호와 동일하게 변경할 수 없습니다."),
    DUPLICATED_EMAIL(BAD_REQUEST, "이미 존재하는 이메일입니다. 메일 수신함을 확인해주세요"),
    INVALID_AUTH_TOKEN(BAD_REQUEST, "인증 토큰이 유효하지 않습니다. 관리자에게 문의하세요"),

    /* 401 UNAUTHORIZED */
    UNAUTHORIZED_MEMBER(UNAUTHORIZED, "현재 내 계정 정보가 존재하지 않습니다"),

    /* 404 NOT_FOUND */
    MEMBER_NOT_FOUND(NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "로그아웃 된 사용자입니다"),
    NOT_FOLLOW(NOT_FOUND, "팔로우 중이지 않습니다"),

    /* 409 CONFLICT */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),

    ;

    private final HttpStatus httpStatus;
    private final String detail;

}
