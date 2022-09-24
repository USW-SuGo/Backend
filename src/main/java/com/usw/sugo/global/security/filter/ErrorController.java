package com.usw.sugo.global.security.filter;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.TokenErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ErrorController {

    @RequestMapping("/jwt-expired")
    public ResponseEntity<Object> sendJWTExpiredError(HttpServletRequest request, HttpServletResponse response) {
        throw new CustomException(TokenErrorCode.JWT_EXPIRED_EXCEPTION);
    }
}
