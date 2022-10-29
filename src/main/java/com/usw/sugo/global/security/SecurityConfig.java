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
            "/user/check-email", "/user/check-loginId",
            "/user/auth", "/user/join",
            "/user/find-id","/user/find-pw",
            "/post/all",
            "/token",
            "/connect", "/message", "/queue/chat/room",
            "/chat/**", "/chat/room", "/app", "/connect/**",
            "/default/**"
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
                ;
        http
                .authorizeRequests()
                .anyRequest().access("hasRole('ROLE_AVAILABLE') or hasRole('ROLE_ADMIN')")
                .and()
                .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterAfter(jwtExceptionFilter(), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    // AuthenticationManager
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(jwtValidator);
    }

    // 인증 필터
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(
                userDetailsRepository, customAuthenticationManager, bCryptPasswordEncoder(),
                userDetailsService, mapper , jwtGenerator, refreshTokenRepository);
    }

    // 인가 필터
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
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