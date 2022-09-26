package com.usw.sugo.global.security.filter;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.global.jwt.JwtValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.usw.sugo.exception.ErrorCode.JWT_MALFORMED_EXCEPTION;


@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더가 필요없는 요청 필터링 - 시작
        String[] whiteListURI = {
                "/user/check-email", "/user/send-authorization-email",
                "/user/verify-authorization-email", "/user/join",
                "/post/all", "/token"};

        for (String whiteList : whiteListURI) {
            if (request.getRequestURI().equals(whiteList)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 헤더가 필요없는 요청 필터링 - 종료

        JSONObject responseJson = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");

        // 헤더가 필요한 요청에 대하여 헤더가 비어있을 때 - 시작
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
            return;
        }
        // 헤더가 필요한 요청에 대하여 헤더가 비어있을 때 - 종료

        // 토큰 검증 - 시작
        try {
            String token = request.getHeader("Authorization").substring(7);
            jwtValidator.validateToken(token);
        }
        catch (BadCredentialsException | SignatureException | NullPointerException ex) {
            try {
                responseJson.put("code", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("message", "토큰이 손상되었습니다.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(responseJson);
                return;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } catch (CustomException | ExpiredJwtException exception) {
            try {
                responseJson.put("code", HttpServletResponse.SC_FORBIDDEN);
                responseJson.put("message", "토큰이 만료되었습니다.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().print(responseJson);
                return;
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        // 토큰 검증 - 종료
        filterChain.doFilter(request, response);
    }
}