package com.usw.sugo.global.security.filter;

import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.usw.sugo.global.exception.ExceptionType.*;

/*
토큰이 필요한 URI에 대한 검증
 */
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    private final List<String> whiteListURI;
    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!isRequestURIWhiteList(request)) {
            if (isNotContainedToken(request, response)) {
                setExceptionResponseForm(response, new CustomException(REQUIRE_TOKEN));
                response.flushBuffer();
                return;
            }
            String token = request.getHeader("Authorization").substring(7);
            if (request.getRequestURI().equals("/token")) {
                if (validateRefreshPayload(response, token)) {
                    response.flushBuffer();
                    return;
                }
            }
            if (validateAccessPayload(response, token)) {
                response.flushBuffer();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isRequestURIWhiteList(HttpServletRequest request) {
        return whiteListURI.contains(request.getRequestURI());
    }

    private boolean isNotContainedToken(HttpServletRequest request, HttpServletResponse response) {
        return request.getHeader("Authorization") == null ||
                !request.getHeader("Authorization").startsWith("Bearer ");
    }

    // AccessToken 만료시 true
    private boolean validateAccessPayload(HttpServletResponse response, String token) {
        try {
            jwtValidator.validateToken(token);
        } catch (CustomException customException) {
            if (customException.getMessage().equals(JWT_MALFORMED_EXCEPTION.getMessage())) {
                setExceptionResponseForm(response, new CustomException(JWT_MALFORMED_EXCEPTION));
                return true;
            } else if (customException.getMessage().equals(JWT_EXPIRED_EXCEPTION.getMessage())) {
                setExceptionResponseForm(response, new CustomException(JWT_EXPIRED_EXCEPTION));
                return true;
            }
        }
        return false;
    }

    // 토큰 재발급 URI + RefreshToken 만료
    private boolean validateRefreshPayload(HttpServletResponse response, String token) {
        try {
            if (jwtValidator.refreshTokenIsExpired(token)) {
                return false;
            }
        } catch (CustomException customException) {
            if (customException.getMessage().equals(JWT_MALFORMED_EXCEPTION.getMessage())) {
                setExceptionResponseForm(response, new CustomException(JWT_MALFORMED_EXCEPTION));
                return true;
            } else if (customException.getMessage().equals(JWT_EXPIRED_EXCEPTION.getMessage())) {
                setExceptionResponseForm(response, new CustomException(JWT_EXPIRED_EXCEPTION));
                return true;
            }
        }
        return false;
    }

    private void setExceptionResponseForm(HttpServletResponse response, CustomException customException) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(400);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Exception", customException.getExceptionType());

        try {
            response.getWriter().print(jsonResponse);
        } catch (IOException e) {
            throw new InternalError(e);
        }
    }
}
