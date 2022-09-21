package com.usw.sugo.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.LoginRequest;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import com.usw.sugo.global.security.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
로그인이 성공하면 Security Context 내부에 인증 객체를 등록해주는 필터
 */
public class JwtAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

    private final CustomAuthenticationManager customAuthenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";

    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
    public static final String HTTP_METHOD = "POST";
    private final ObjectMapper mapper;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/user/login", HTTP_METHOD);

    public JwtAuthorizationFilter(CustomAuthenticationManager customAuthenticationManager,
                                  BCryptPasswordEncoder bCryptPasswordEncoder,
                                  UserDetailsService userDetailsService,
                                  ObjectMapper mapper, JwtGenerator jwtGenerator) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, customAuthenticationManager);

        this.customAuthenticationManager = customAuthenticationManager;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mapper = mapper;
        this.jwtGenerator = jwtGenerator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        System.out.println("인증 필터 동작");

        if (!request.getMethod().equals(HTTP_METHOD) || !request.getContentType().equals("application/json")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = mapper.readValue(StreamUtils.copyToString(request.getInputStream(),
                StandardCharset.UTF_8), LoginRequest.class);

        String requestEmail = loginRequest.getEmail();
        String requestPassword = loginRequest.getPassword();

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsService.loadUserByUsername(requestEmail);

        Long userId = userDetailsImpl.getId();
        String email = userDetailsImpl.getEmail();
        String nickname = userDetailsImpl.getNickname();

        if (email == null || requestPassword == null) throw new AuthenticationServiceException("DATA IS MISS");

        if (bCryptPasswordEncoder.matches(requestPassword, userDetailsImpl.getPassword())) {

            // setDetails(request, authToken);
            System.out.println("비밀번호가 올바름");

            String accessToken = jwtGenerator.createTestAccessToken(userId, nickname, email);
            String refreshToken = jwtGenerator.createTestRefreshToken(userId);

            Map<String, String> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);

            response.setHeader("Authorization", result.toString());
            response.flushBuffer();

            // return this.getAuthenticationManager().authenticate(authToken);
        }
        else {
            System.out.println("비밀번호가 올바르지 않음");
        }
        return null;
    }
}

