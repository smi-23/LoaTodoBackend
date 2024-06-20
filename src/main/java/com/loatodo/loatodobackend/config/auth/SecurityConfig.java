package com.loatodo.loatodobackend.config.auth;

import com.loatodo.loatodobackend.util.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // 시큐리티 필터 메서드
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // 접근 권한 설정
        http
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().permitAll()
                );

        // OAuth 2.0 로그인 방식 설정
        http
                .oauth2Login((auth) -> auth.loginPage("/oauth-login/login")
                        .defaultSuccessUrl("/oauth-login")
                        .failureUrl("/oauth-login/login")
                        .permitAll());

        http
                .logout((auth) -> auth
                        .logoutUrl("/oauth-login/logout"));

        http
                .formLogin(AbstractHttpConfigurer::disable)// FormLogin 사용 X
                .httpBasic(AbstractHttpConfigurer::disable)// httpBasic 사용 X
                .csrf(AbstractHttpConfigurer::disable); // csrf 보안 사용 X
//                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //세션 사용 X

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
