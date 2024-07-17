package com.loatodo.loatodobackend.jwt;


import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.util.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "RefreshToken";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Duration ACCESS_TOKEN_VALIDITY = Duration.ofSeconds(20);
    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(30);

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // access토큰 생성
    public String createAccessToken(User user) {
        Date date = new Date();
        String username = user.getUsername();
        String name = user.getName();
        String email = user.getEmail();
        UserRole role = user.getRole();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .claim(AUTHORIZATION_KEY, role)
                        .claim("name", name)
                        .claim("email", email)
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_VALIDITY.toMillis()))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // refresh토큰 생성
    public String createRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(username, refreshToken, REFRESH_TOKEN_VALIDITY.toMillis(), TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    // claim 가져오기
    public Claims getClaims(HttpServletRequest request) {
        // 토큰을 헤더에서 가져옴
        String accessToken = resolveToken(request, AUTHORIZATION_HEADER);
        if (accessToken == null || !validateAccessToken(accessToken)) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
        return getUserInfoFromToken(accessToken);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request, String header) {
        String bearerToken = request.getHeader(header);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰 검증
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setAllowedClockSkewSeconds(300).setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void refreshTokens(User user, HttpServletRequest request, HttpServletResponse response) {
        // 리프레시토큰 검증
        validateRefreshToken(user.getUsername(), request);

        // 해당 유저정보를 바탕으로 토큰을 재발급
        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user.getUsername());
        log.info("반환된 액세스토큰 {}", accessToken);
        log.info("반환된 리프레시토큰 {}", refreshToken);

        // jwtToken 헤더에 넣어주기
        response.addHeader("Authorization", accessToken);
        response.addHeader("RefreshToken", refreshToken);
    }

    // 리프레시토큰 검증
    public void validateRefreshToken(String username, HttpServletRequest request) {
        // 인자로 받아온 토큰이 레디스에 저장되어 있는지
        String redisToken = (String) redisTemplate.opsForValue().get(username);
        if (redisToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        String refreshToken =  request.getHeader(REFRESH_HEADER);
        if (!refreshToken.equals(redisToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}
