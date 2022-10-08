package com.usw.sugo.global.exception;


public class CustomException extends BaseException {
    public CustomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
