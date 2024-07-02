package com.loatodo.loatodobackend.jwt;


import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.util.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_HEADER = "Refresh-Token";
    public static final String AUTHORIZATION_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Duration ACCESS_TOKEN_VALIDITY = Duration.ofDays(1);
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
        Date date = new Date();

        String refreshToken = BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username)
                        .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_VALIDITY.toMillis()))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
        redisTemplate.opsForValue().set(username, refreshToken, REFRESH_TOKEN_VALIDITY.toMillis(), TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    // claim 가져오기
    public Claims getClaims(HttpServletRequest request) {
        String jwtToken = resolveToken(request, AUTHORIZATION_HEADER);
        if (jwtToken == null || !validateToken(jwtToken)) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
        return getUserInfoFromToken(jwtToken);
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
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // 리프레시 토큰 검증
    public boolean validateRefreshToken(String refreshToken) {
        try {
            String username = getUserInfoFromToken(refreshToken).getSubject();
            String redisToken = (String) redisTemplate.opsForValue().get(username);
            if (refreshToken.equals(redisToken)) {
                return true;
            }
        } catch (Exception e) {
            log.error("Invalid refresh token", e);
        }
        return false;
    }

    // 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰 발급
    public Map<String, String> refreshTokens(HttpServletRequest request) {
        String refreshToken = resolveToken(request, REFRESH_HEADER);
        if (refreshToken == null || !validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }
        Claims claims = getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();
        User user = new User(); // User 객체를 DB에서 가져와야 함
        user.setUsername(username);
        user.setRole(UserRole.valueOf(claims.get(AUTHORIZATION_KEY).toString()));
        // name과 email 등 필요한 정보도 설정

        String newAccessToken = createAccessToken(user);
        String newRefreshToken = createRefreshToken(username);

        // 새 리프레시 토큰을 Redis에 저장
        redisTemplate.opsForValue().set(username, newRefreshToken, REFRESH_TOKEN_VALIDITY.toMillis(), TimeUnit.MILLISECONDS);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }
}
