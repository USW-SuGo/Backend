package com.usw.sugo.global.security.filter;

import com.usw.sugo.global.exception.CustomException;
import org.json.JSONObject;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.usw.sugo.global.exception.ExceptionType.REQUIRE_TOKEN;

/*
토큰이 필요한 URI에 대한 검증
 */
public class AuthorizationFilter extends OncePerRequestFilter {

    private final List<String> whiteListURI = List.of(
            "/user/check-email", "/user/check-loginId", "/user/auth", "/user/join", "/user/login",
            "/user/find-id", "/user/find-pw", "/post/all", "/token");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!validateRequestUriIncludedWhiteList(request)) {
            if (!validateRequestHeaderIncludedToken(request, response)) {
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateRequestUriIncludedWhiteList(HttpServletRequest request) {
        return whiteListURI.contains(request.getRequestURI());
    }

    private boolean validateRequestHeaderIncludedToken(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader("Authorization") == null ||
                !request.getHeader("Authorization").startsWith("Bearer ")) {
            setExceptionResponseForm(response, new CustomException(REQUIRE_TOKEN));
        }
        return true;
    }

    private void setExceptionResponseForm(HttpServletResponse response, CustomException customException) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(400);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Exception", customException.getExceptionType());
        jsonResponse.put("Message", customException.getMessage());

        try {
            response.getWriter().print(jsonResponse);
        } catch (IOException e) {
            throw new InternalError(e);
        }
    }

    private void setSuccessResponseForm(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
    }
}
