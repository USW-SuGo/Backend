package com.usw.sugo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usw.sugo.domain.majoruser.user.repository.UserDetailsRepository;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.global.security.authentication.CustomAuthenticationManager;
import com.usw.sugo.global.security.authentication.CustomAuthenticationProvider;
import com.usw.sugo.global.security.filter.JwtExceptionFilter;
import com.usw.sugo.global.security.filter.JwtAuthenticationFilter;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.jwt.JwtValidator;
import com.usw.sugo.global.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsRepository userDetailsRepository;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final UserDetailsService userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtValidator jwtValidator;
    private final JwtGenerator jwtGenerator;
    private final JwtResolver jwtResolver;
    private final ObjectMapper mapper;
    private final RefreshTokenRepository refreshTokenRepository;

    String[] whiteListURI = {
            "/user/check-email", "/user/send-authorization-email",
            "/user/verify-authorization-email/**", "/user/join",
            "/post/all", "/token", "/chat/*"
    };

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
                .antMatchers(whiteListURI).permitAll()
                .anyRequest().access("hasRole('ROLE_AVAILABLE') or hasRole('ROLE_ADMIN')")
                .and()
                .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterAfter(jwtExceptionFilter(), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    // AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(jwtValidator);
    }

    // 인증 필터
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(
                userDetailsRepository, customAuthenticationManager, bCryptPasswordEncoder(),
                userDetailsService, mapper , jwtGenerator, refreshTokenRepository);
    }

    // 인가 필터
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(
                userDetailsService, customAuthenticationManager, jwtResolver, jwtValidator);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*");
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}