package com.usw.sugo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usw.sugo.domain.user.user.repository.UserDetailsRepository;
import com.usw.sugo.global.jwt.JwtGenerator;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.security.filter.JwtFilter;
import com.usw.sugo.global.security.filter.LoginFilter;
import com.usw.sugo.global.util.factory.BCryptPasswordFactory;
import java.util.List;
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

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationManager customAuthenticationManager;
    private final UserDetailsRepository userDetailsRepository;
    private final UserDetailsService userDetailsService;
    private final JwtGenerator jwtGenerator;
    private final JwtResolver jwtResolver;
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
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

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

    public LoginFilter loginFilter() {
        return new LoginFilter(
            customAuthenticationManager,
            userDetailsRepository,
            userDetailsService,
            bCryptPasswordEncoder(),
            jwtGenerator, mapper);
    }

    public JwtFilter jwtFilter() {
        return new JwtFilter(whiteListURI, jwtResolver, userDetailsService);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return BCryptPasswordFactory.getBCryptPasswordEncoder();
    }
}