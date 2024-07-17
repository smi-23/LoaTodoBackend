package com.loatodo.loatodobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 모든 도메인 및 모든 HTTP 메소드에 대한 허용을 설정합니다.
//        config.addAllowedOrigin("배포 url");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        // 클라이언트에서 접근할 수 있는 헤더를 명시적으로 설정
        config.addExposedHeader("Authorization");
        config.addExposedHeader("RefreshToken");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
