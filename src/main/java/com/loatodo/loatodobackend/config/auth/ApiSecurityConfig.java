package com.loatodo.loatodobackend.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    @Bean(name = "apiSecurityFilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("api/user/**")
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/api/user/**").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable); // csrf 보안 사용 X
        return http.build();
    }
}
