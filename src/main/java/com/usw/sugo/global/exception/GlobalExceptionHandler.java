package com.usw.sugo.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "NO_CATCH_ERROR";
        String className = e.getClass().getName();
        String message = e.getMessage();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        String className = e.getClass().getName();
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleBindValidationException(Exception e) {
        String className = e.getClass().getName();
        ErrorCode errorCode = ErrorCode.PARAM_VALID_ERROR;
        String message = "";

        if (e instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else if (e instanceof BindException) {
            message = ((BindException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(message)
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String className = e.getClass().getName();
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(e.getMessage())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String className = e.getClass().getName();
        ErrorCode errorCode = ErrorCode.USER_BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(value = {HttpServerErrorException.InternalServerError.class})
    public ResponseEntity<ErrorResponse> InternalServerError(HttpServerErrorException.InternalServerError e) {
        String className = e.getClass().getName();
        ErrorCode errorCode = ErrorCode.SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> IllegalArgumentException(IllegalArgumentException e) {
        String className = e.getClass().getName();
        ErrorCode errorCode = ErrorCode.USER_BAD_REQUEST;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
