package com.usw.sugo.global.config.security.filter;

import static com.nimbusds.jose.util.StandardCharset.UTF_8;
import static com.usw.sugo.global.exception.ExceptionType.PASSWORD_NOT_CORRECT;
import static com.usw.sugo.global.exception.ExceptionType.USER_NOT_EXIST;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.LoginRequestForm;
import com.usw.sugo.domain.user.user.repository.UserDetailsRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtGenerator;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

/*
로그인 시 검증 후 ContextHolder에 인증객체 저장
 */
public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationManager customAuthenticationManager;
    private final UserDetailsRepository userDetailsRepository;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtGenerator jwtGenerator;
    private final ObjectMapper mapper;
    public static final String HTTP_METHOD = "POST";
    public static final String REQUEST_URI = "/user/login";
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
        new AntPathRequestMatcher("/user/login", HTTP_METHOD);

    public LoginFilter(AuthenticationManager customAuthenticationManager,
        UserDetailsRepository userDetailsRepository, UserDetailsService userDetailsService,
        BCryptPasswordEncoder bCryptPasswordEncoder, JwtGenerator jwtGenerator,
        ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, null);
        this.customAuthenticationManager = customAuthenticationManager;
        this.userDetailsRepository = userDetailsRepository;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.mapper = objectMapper;
    }

    private boolean validateURI(HttpServletRequest request) {
        if (request.getRequestURI().equals(REQUEST_URI) && request.getMethod().equals(HTTP_METHOD)
            && request.getContentType().equals("application/json")) {
            return true;
        }
        throw new AuthenticationServiceException("Requested method not supported");
    }

    private LoginRequestForm extractLoginRequestForm(HttpServletRequest request)
        throws IOException {
        return mapper.readValue(
            StreamUtils.copyToString(
                request.getInputStream(), UTF_8
            ), LoginRequestForm.class
        );
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response
    ) throws AuthenticationException, IOException {

        if (validateURI(request)) {
            final LoginRequestForm loginRequestForm = extractLoginRequestForm(request);
            final String requestLoginId = loginRequestForm.getLoginId();
            final String requestPassword = loginRequestForm.getPassword();

            final UserDetails userDetails = userDetailsService.loadUserByUsername(requestLoginId);

            // 비밀번호가 일치할 때
            if (bCryptPasswordEncoder.matches(requestPassword, userDetails.getPassword())) {
                if (userDetailsRepository.findByLoginId(userDetails.getUsername()).isEmpty()) {
                    setExceptionResponseForm(response, new CustomException(USER_NOT_EXIST));
                    response.flushBuffer();
                    return null;
                }

                final User user = userDetailsRepository.findByLoginId(
                    userDetails.getUsername()
                ).get();

                final Authentication authentication = createAuthenticationByLoginForm(
                    user.getLoginId(), user.getPassword()
                );

                final String accessToken = jwtGenerator.generateAccessToken(user);
                final String refreshToken = jwtGenerator.generateRefreshToken(user);

                setSuccessResponseForm(response);
                enrollContextHolderForAuthentication(authentication);
                response.setHeader(
                    "Authorization", jwtGenerator.wrapTokenPair(
                        accessToken, refreshToken
                    ).toString()
                );
                response.flushBuffer();
                return authentication;
            } else {
                setExceptionResponseForm(response, new CustomException(PASSWORD_NOT_CORRECT));
                response.flushBuffer();
                return null;
            }
        }
        throw new AuthenticationServiceException("Authorization method not supported");
    }

    private Authentication createAuthenticationByLoginForm(String loginId, String password) {
        return customAuthenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginId, password)
        );
    }

    private void enrollContextHolderForAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setExceptionResponseForm(HttpServletResponse response, CustomException customException) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(400);
        final JSONObject jsonResponse = new JSONObject();
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