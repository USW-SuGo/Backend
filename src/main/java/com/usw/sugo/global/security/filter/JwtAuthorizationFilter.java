package com.usw.sugo.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.LoginRequest;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import com.usw.sugo.global.security.authentication.UserDetailsImpl;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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

            String accessToken = jwtGenerator.createTestAccessToken(userId, nickname, email);
            String refreshToken = jwtGenerator.createTestRefreshToken(userId);

            Map<String, String> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);

            response.setHeader("Authorization", result.toString());
            response.flushBuffer();
        }
        else {
            JSONObject responseJson = new JSONObject();
            response.setContentType("application/json;charset=UTF-8");
            try {
                responseJson.put("ErrorCode", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("Message", "비밀번호가 일치하지 않거나 존재하지 않는 사용자 입니다.");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
        }

        return null;
    }
}

