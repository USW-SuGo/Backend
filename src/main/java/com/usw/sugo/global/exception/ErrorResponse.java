package com.usw.sugo.global.exception;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ErrorResponse {

    private String exception;

    private String message;

    private int status;

    private String error;
}
