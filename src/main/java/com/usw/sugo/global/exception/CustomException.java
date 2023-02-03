package com.usw.sugo.global.exception;


import lombok.Getter;

@Getter
public class CustomException extends BaseException {
    public CustomException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
