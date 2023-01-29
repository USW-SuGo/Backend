package com.usw.sugo.global.security.filter;

import com.usw.sugo.global.exception.CustomException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.usw.sugo.global.exception.ExceptionType.REQUIRE_TOKEN;

/*
매 요청마다 JWT 가 유효한지 검증하고, 유효할 시 해당 유저에 Security Context 를 인가 해주는 필터
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final List<String> whiteListURI = List.of(
            "/user/check-email", "/user/check-loginId", "/user/auth", "/user/join", "/user/login",
            "/user/find-id", "/user/find-pw", "/post/all", "/token");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (validateHeader(request) || validateURI(request)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean validateHeader(HttpServletRequest request) {
        if (request.getHeader("Authorization") == null ||
                !request.getHeader("Authorization").contains("Bearer ")) {
            throw new CustomException(REQUIRE_TOKEN);
        }
        return true;
    }

    private boolean validateURI(HttpServletRequest request) {
        return whiteListURI.stream()
                .noneMatch(whiteListURI::contains);
    }
}