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
    public ResponseEntity<ExceptionInformation> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "NO_CATCH_ERROR";
        String className = e.getClass().getName();
        String message = e.getMessage();

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, status);
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<ExceptionInformation> handleBaseException(BaseException e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = e.getExceptionType();

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(exceptionType.getMessage())
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, exceptionType.getStatus());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ExceptionInformation> handleBindValidationException(Exception e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = ExceptionType.PARAM_VALID_ERROR;
        String message = "";

        if (e instanceof MethodArgumentNotValidException) {
            message = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else if (e instanceof BindException) {
            message = ((BindException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(message)
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, exceptionType.getStatus());
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ExceptionInformation> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = ExceptionType.METHOD_NOT_ALLOWED;

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(e.getMessage())
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, exceptionType.getStatus());
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionInformation> HttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = ExceptionType.USER_BAD_REQUEST;

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(exceptionType.getMessage())
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, exceptionType.getStatus());
    }

    @ExceptionHandler(value = {HttpServerErrorException.InternalServerError.class})
    public ResponseEntity<ExceptionInformation> InternalServerError(HttpServerErrorException.InternalServerError e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = ExceptionType.SERVER_ERROR;

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(exceptionType.getMessage())
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, exceptionType.getStatus());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ExceptionInformation> IllegalArgumentException(IllegalArgumentException e) {
        String className = e.getClass().getName();
        ExceptionType exceptionType = ExceptionType.USER_BAD_REQUEST;

        ExceptionInformation exceptionInformation = ExceptionInformation.builder()
                .exception(className.substring(className.lastIndexOf(".") + 1))
                .message(exceptionType.getMessage())
                .status(exceptionType.getStatus().value())
                .error(exceptionType.getStatus().getReasonPhrase())
                .build();

        return new ResponseEntity<>(exceptionInformation, exceptionType.getStatus());
    }
}
