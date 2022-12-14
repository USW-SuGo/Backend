package com.usw.sugo.global.security.filter;

import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.jwt.JwtValidator;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

import static com.usw.sugo.global.exception.ExceptionType.*;

/*
매 요청마다 JWT 가 유효한지 검증하고, 유효할 시 해당 유저에 Security Context 를 인가 해주는 필터
 */
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationManager authenticationManager;
    private final JwtResolver jwtResolver;
    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // AccessToken이 필요없는 요청 필터링 - 시작
        String[] whiteListURI = {
                "/user/check-email", "/user/check-loginId", "/user/login",
                "/user/auth", "/user/join",
                "/user/find-id", "/user/find-pw",
                "/post/all",
                "/token"
        };
        for (String whiteList : whiteListURI) {
            if (request.getRequestURI().equals(whiteList)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (request.getHeader("Authorization") == null) {
            throw new CustomException(REQUIRE_TOKEN);
        }

        String token = request.getHeader("Authorization").substring(7);
        jwtValidator.validateToken(token);

        String loginId = jwtResolver.jwtResolveToUserLoginId(token);

        // AccessToken 에 담긴 정보가 DB 에 존재하는 유저일 때
        try {
            UserDetails requestUserDetails = userDetailsService.loadUserByUsername(loginId);
            System.out.println("LoginId 조회 1");
        } catch (CustomException | NoSuchElementException e) {
            JSONObject responseJson = new JSONObject();
            try {
                responseJson.put("code", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("message", ExceptionType.USER_NOT_EXIST.toString());
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
            response.flushBuffer();
            return;
        }

        UserDetails requestUserDetails = userDetailsService.loadUserByUsername(loginId);
        System.out.println("LoginId 조회 2");

        // JWT 를 바탕으로 인증 객체 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                requestUserDetails.getUsername(), requestUserDetails.getPassword());

        // Authorities 부여
        Authentication auth = authenticationManager.authenticate(authToken);

        // SecurityContextHolder 에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}


