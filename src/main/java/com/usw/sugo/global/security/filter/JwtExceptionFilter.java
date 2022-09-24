package com.usw.sugo.global.security.filter;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.TokenErrorCode;
import com.usw.sugo.global.jwt.JwtValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

        JSONObject responseJson = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");

        // return 구문이 없으니까 Filter 메서드가 끝나지 않았다.
        // 특정 구문을 실행하고 끝내고 싶으면 return; 을 추가한다.
        if (request.getHeader("Authorization") == null) {
            try {
                responseJson.put("code", new CustomException(TokenErrorCode.JWT_MALFORMED_EXCEPTION).getErrorCode().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                responseJson.put("message", new CustomException(TokenErrorCode.JWT_MALFORMED_EXCEPTION).getMessage());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
            return;
        }

        try {
            String token = request.getHeader("Authorization").substring(6);
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
        filterChain.doFilter(request, response);
    }
}