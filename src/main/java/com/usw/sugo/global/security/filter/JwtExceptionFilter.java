package com.usw.sugo.global.security.filter;

import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.usw.sugo.global.exception.ErrorCode.JWT_MALFORMED_EXCEPTION;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;


@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String[] whiteListURI = {
                "/user/check-email", "/user/check-loginId", "/user/login",
                "/user/auth", "/user/join",
                "/user/find-id", "/user/find-pw",
                "/post/all",
                "/token"
        };

        for (String whiteList : whiteListURI) {
            if (request.getRequestURI().contains(whiteList)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        JSONObject responseJson = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");

        if (request.getHeader("Authorization") == null) {
            try {
                responseJson.put("code", new CustomException(JWT_MALFORMED_EXCEPTION).getErrorCode().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                responseJson.put("message", new CustomException(JWT_MALFORMED_EXCEPTION).getMessage());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            response.setStatus(SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
            return;
        }

        try {
            String token = request.getHeader("Authorization").substring(7);
            jwtValidator.validateToken(token);
        } catch (BadCredentialsException | SignatureException | NullPointerException ex) {
            try {
                responseJson.put("code", SC_BAD_REQUEST);
                responseJson.put("message", "토큰이 손상되었습니다.");
                response.setStatus(SC_BAD_REQUEST);
                response.getWriter().print(responseJson);
                return;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (CustomException | ExpiredJwtException exception) {
            try {
                responseJson.put("code", SC_FORBIDDEN);
                responseJson.put("message", "토큰이 만료되었습니다.");
                response.setStatus(SC_FORBIDDEN);
                response.getWriter().print(responseJson);
                return;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        // 토큰 검증 - 종료
        filterChain.doFilter(request, response);
    }
}