package com.usw.sugo.global.security.filter;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.exception.ErrorCode;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.jwt.JwtValidator;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더가 필요없는 요청 필터링 - 시작
        String[] whiteListURI = {
                "/user/check-email", "/user/send-authorization-email",
                "/user/verify-authorization-email", "/user/join",
                "/post/all", "/token", "/chat", "/ws/chat", "/socket/chat"};

        for (String whiteList : whiteListURI) {
            if (request.getRequestURI().equals(whiteList)) {
                System.out.println("화이트 리스트 (AuthenticationFilter)");
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 헤더가 필요없는 요청 필터링 - 종료

        // 헤더가 필요한 요청에 대하여 헤더가 비어있을 때 - 시작
        if (request.getHeader("Authorization") == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // 헤더가 필요한 요청에 대하여 헤더가 비어있을 때 - 종료

        String token = request.getHeader("Authorization").substring(7);

        try {
            jwtValidator.validateToken(token);
        } catch (BadCredentialsException | SignatureException | CustomException | ExpiredJwtException ex) {
            filterChain.doFilter(request, response);
            return;
        }
        // 해당 AccessToken Payload 유효하다면 인가 및 인증객체 저장
        String email = jwtResolver.jwtResolveToUserEmail(token);

        try {
            UserDetails requestUserDetails = userDetailsService.loadUserByUsername(email);
        } catch (CustomException | NoSuchElementException e) {
            JSONObject responseJson = new JSONObject();
            try {
                responseJson.put("code", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("message", ErrorCode.USER_NOT_EXIST.toString());
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
            return;
        }

        UserDetails requestUserDetails = userDetailsService.loadUserByUsername(email);

        // JWT 를 바탕으로 인증 객체 생성
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(requestUserDetails.getUsername(), requestUserDetails.getPassword());
        // Authorities 부여
        Authentication auth = authenticationManager.authenticate(authToken);

        // SecurityContextHolder 에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}


