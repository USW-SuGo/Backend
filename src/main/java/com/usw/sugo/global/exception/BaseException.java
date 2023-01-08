package com.usw.sugo.global.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {

    @Getter
    private ExceptionType exceptionType;

    public BaseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
