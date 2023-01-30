package com.usw.sugo.global.security.filter;

import com.usw.sugo.global.exception.CustomException;
import org.springframework.security.core.AuthenticationException;
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
        if (validateURI(request)) {
            validateHeader(request);
        }
        filterChain.doFilter(request, response);
    }

    private boolean validateURI(HttpServletRequest request) {
        return !whiteListURI.contains(request.getRequestURI());
    }

    private boolean validateHeader(HttpServletRequest request) {
        if (request.getHeader("Authorization") == null ||
                !request.getHeader("Authorization").substring(7).equals("Bearer ")) {
            throw new CustomException(REQUIRE_TOKEN);
        }
        return true;
    }
}
