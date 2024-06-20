//package com.loatodo.loatodobackend.util;
//
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//
//@Component
//public class JwtUtill {
//    @Value("${jwt.secret.key}")
//    private String secretKey;
//    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; // 15분
//    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일
//
//    // 토큰에서 사용자 정보 가져오기
//    public Claims getUserInfoFromToken(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
//    }
//}
