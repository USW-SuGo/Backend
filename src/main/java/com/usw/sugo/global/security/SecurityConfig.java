package com.usw.sugo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usw.sugo.domain.user.user.repository.UserDetailsRepository;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.jwt.JwtValidator;
import com.usw.sugo.global.security.filter.AuthorizationFilter;
import com.usw.sugo.global.security.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationManager customAuthenticationManager;
    private final UserDetailsRepository userDetailsRepository;
    private final UserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    private final JwtValidator jwtValidator;
    private final ObjectMapper mapper;

    private final List<String> whiteListURI = List.of(
            "/user/check-email", "/user/check-loginId", "/user/auth", "/user/join", "/user/login",
            "/user/find-id", "/user/find-pw", "/post/all");

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .headers().frameOptions().disable()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .authorizeRequests()
                .antMatchers(whiteListURI.toString()).permitAll()
        ;
        http
                .addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        // .addFilterBefore(authorizationFilter(), OncePerRequestFilter.class); NPE가 발생하는 코드 왜일까..
        return http.build();
    }

    // 인증 필터 :--> 로그인 필터
    public LoginFilter loginFilter() {
        return new LoginFilter(
                customAuthenticationManager,
                userDetailsRepository,
                userDetailsService,
                bCryptPasswordEncoder(),
                jwtGenerator, mapper);
    }

    // 인가 필터 :--> JWT 필터
    public AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter(whiteListURI, jwtValidator);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}