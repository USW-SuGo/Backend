package com.usw.sugo.exception;


public class CustomException extends BaseException {
    public CustomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
