package com.usw.sugo.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.LoginRequest;
import com.usw.sugo.domain.majoruser.user.repository.UserDetailsRepository;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import com.usw.sugo.global.security.authentication.UserDetailsImpl;
import org.json.JSONException;
import org.json.JSONObject;
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

    private final UserDetailsRepository userDetailsRepository;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    public static final String HTTP_METHOD = "POST";
    private final ObjectMapper mapper;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher("/user/login", HTTP_METHOD);

    public JwtAuthorizationFilter(
            UserDetailsRepository userDetailsRepository,
            CustomAuthenticationManager customAuthenticationManager,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserDetailsService userDetailsService,
            ObjectMapper mapper, JwtGenerator jwtGenerator,
            RefreshTokenRepository refreshTokenRepository) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, customAuthenticationManager);

        this.userDetailsRepository = userDetailsRepository;
        this.customAuthenticationManager = customAuthenticationManager;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mapper = mapper;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (!request.getMethod().equals(HTTP_METHOD) || !request.getContentType().equals("application/json")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = mapper.readValue(StreamUtils.copyToString(request.getInputStream(),
                StandardCharset.UTF_8), LoginRequest.class);

        String requestLoginId = loginRequest.getLoginId();
        String requestPassword = loginRequest.getPassword();

        try {
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsService.loadUserByUsername(requestLoginId);
        }
        catch (CustomException exception) {
            JSONObject responseJson = new JSONObject();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try {
                responseJson.put("ErrorCode", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("Message", "존재하지 않는 사용자입니다.");
                response.getWriter().print(responseJson);
                return null;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetailsService.loadUserByUsername(requestLoginId);

        Long userId = userDetailsImpl.getId();
        String loginId = userDetailsImpl.getLoginId();
        String email = userDetailsImpl.getEmail();
        String nickname = userDetailsImpl.getNickname();
        String status = userDetailsImpl.getStatus();

        // 이메일 인증을 받지 않은 사용자의 로그인일 때
        if (status.equals("NOT_AUTH")) {
            JSONObject responseJson = new JSONObject();
            response.setContentType("application/json;charset=UTF-8");
            try {
                responseJson.put("ErrorCode", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("Message", "이메일 인증을 받지 않은 사용자입니다. 웹메일에 이메일 인증 링크를 확인해주세요.");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
            return null;
        }

        if (email == null || requestPassword == null) throw new AuthenticationServiceException("DATA IS MISS");

        if (bCryptPasswordEncoder.matches(requestPassword, userDetailsImpl.getPassword())) {
            // 리프레시 토큰을 한 번도 발급받지 않은 유저일 때 -> 리프레시 토큰 신규 발급
            if (refreshTokenRepository.findByUserId(userId).isEmpty()) {
                String accessToken = jwtGenerator.createAccessTokenInFilter(userId, loginId, nickname, email, status);
                String refreshToken = jwtGenerator.createRefreshTokenInFilter(userId);

                Map<String, String> result = new HashMap<>();
                result.put("AccessToken", accessToken);
                result.put("RefreshToken", refreshToken);
                response.setHeader("Authorization", result.toString());
                response.flushBuffer();
            }
            // 리프레시 토큰을 발급받은 적이 있는 유저일 때 -> 리프래시 토큰은 갱신한다.
            else {
                User user = userDetailsRepository.findByEmail(email).get();

                String accessToken = jwtGenerator.createAccessTokenInFilter(userId, loginId, nickname, email, status);
                String refreshToken = jwtGenerator.updateRefreshToken(user);

                Map<String, String> result = new HashMap<>();
                result.put("AccessToken", accessToken);
                result.put("RefreshToken", refreshToken);

                response.setHeader("Authorization", result.toString());
                response.flushBuffer();
            }
        }
        else {
            JSONObject responseJson = new JSONObject();
            response.setContentType("application/json;charset=UTF-8");
            try {
                responseJson.put("ErrorCode", HttpServletResponse.SC_BAD_REQUEST);
                responseJson.put("Message", "비밀번호가 일치하지 않습니다.");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(responseJson);
        }
        return null;
    }
}

