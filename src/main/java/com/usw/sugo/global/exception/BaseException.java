package com.usw.sugo.global.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

    @Getter
    private ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
