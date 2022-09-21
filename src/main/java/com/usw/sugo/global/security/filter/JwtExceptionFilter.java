package com.usw.sugo.global.security.filter;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.TokenErrorCode;
import com.usw.sugo.exception.UserErrorCode;
import com.usw.sugo.global.jwt.JwtValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.usw.sugo.exception.TokenErrorCode.JWT_EXPIRED_EXCEPTION;
import static com.usw.sugo.exception.TokenErrorCode.JWT_MALFORMED_EXCEPTION;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String[] whiteListURI = {
                "/user/check-email", "/user/send-authorization-email",
                "/user/verify-authorization-email", "/user/detail-join",
                "/post/all"};

        for (String whiteList : whiteListURI) {
            if (request.getRequestURI().equals(whiteList)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // return 구문이 없으니까 Filter 메서드가 끝나지 않았다.
        // 특정 구문을 실행하고 끝내고 싶으면 return; 을 추가한다.
        if (request.getHeader("Authorization") == null) {
            System.out.println("헤더가 없는 요청");
            System.out.println("예외 필터까지 들어옴");
            // throw new CustomException(JWT_MALFORMED_EXCEPTION);
            response.sendError(400);
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("테스트1");

        try {
            String token = request.getHeader("Authorization").substring(6);
            jwtValidator.validateToken(token);
        } catch (BadCredentialsException | SignatureException | NullPointerException ex) {
            response.sendError(400);
        } catch (CustomException exception) {
            response.sendError(400);
        } catch (ExpiredJwtException exception) {
            response.sendError(403);
        }


        filterChain.doFilter(request, response);

    }
}