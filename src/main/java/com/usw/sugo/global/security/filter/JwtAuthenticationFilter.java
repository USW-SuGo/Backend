package com.usw.sugo.global.security.filter;

import com.usw.sugo.exception.CustomException;
import com.usw.sugo.global.jwt.JwtValidator;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import com.usw.sugo.global.jwt.JwtResolver;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        System.out.println("인가 필터 동작");

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
        
        try {
            request.getHeader("Authorization").substring(6);
        } catch (NullPointerException exception) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization").substring(6);

        try {
            jwtValidator.validateToken(token);
        } catch (BadCredentialsException | SignatureException | CustomException | ExpiredJwtException ex) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtResolver.jwtResolveToUserEmail(token);

        UserDetails requestUserDetails = userDetailsService.loadUserByUsername(email);

        // JWT 를 바탕으로 인증 객체 생성
        Authentication authToken =
                new UsernamePasswordAuthenticationToken(requestUserDetails.getUsername(), requestUserDetails.getPassword());

        // Authorities 부여
        Authentication auth = authenticationManager.authenticate(authToken);

        // SecurityContextHolder 에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        // System.out.println("SecurityContextHolder.getContext() = , " + SecurityContextHolder.getContext());

        // authenticationManager.authenticate(jwtAuthenticationToken);
        // setDetails(request, jwtAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}


