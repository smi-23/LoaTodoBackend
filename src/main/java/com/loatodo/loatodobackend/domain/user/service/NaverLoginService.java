package com.loatodo.loatodobackend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loatodo.loatodobackend.domain.user.dto.LoginResponseDto;
import com.loatodo.loatodobackend.domain.user.dto.naver.NaverProfile;
import com.loatodo.loatodobackend.domain.user.dto.naver.NaverToken;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import com.loatodo.loatodobackend.util.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NaverLoginService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.naver.client_id}")
    String naver_client_id;

    @Value("${spring.security.naver.client_secret}")
    String naver_client_secret;

    @Value("${spring.security.naver.redirect_uri}")
    String naver_redirect_uri;

    public NaverToken getNaverToken(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://nid.naver.com/oauth2.0/token")
                .build();

        String naverTokenResponse = webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", naver_client_id)
                        .with("client_secret", naver_client_secret)
                        .with("code", code))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        NaverToken naverToken = null;
        try {
            naverToken = objectMapper.readValue(naverTokenResponse, NaverToken.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing NaverToken JSON", e);
        }
        return naverToken;
    }

    public ResponseEntity<Message> loginWithNaver(String code, HttpServletResponse response) {
        NaverToken naverToken = getNaverToken(code);

        WebClient webClient = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/nid/me")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + naverToken.getAccess_token())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String naverProfileResponse = webClient.post()
                .retrieve()
                .bodyToMono(String.class)
                .block();

        NaverProfile naverProfile = null;
        try {
            naverProfile = objectMapper.readValue(naverProfileResponse, NaverProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String username = "Naver" + "_" + naverProfile.getResponse().getId();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("이미 네이버아이디로 회원정보가 등록되어있습니다. 로그인만 진행합니다.");
        } else {
            // 사용자가 없을 경우 새로 생성
            user = User.builder()
                    .username(username)
                    .name(naverProfile.getResponse().getName())
                    .email(naverProfile.getResponse().getEmail())
                    .provider("Naver")
                    .providerId(String.valueOf(naverProfile.getResponse().getId()))
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
            log.info("네이버아이디로 회원가입이 완료되었습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        // jwtToken 헤더에 넣어주기
        response.addHeader("Authorization", accessToken);
        response.addHeader("RefreshToken", refreshToken);

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        log.info("네이버 로그인 되었습니다.");

        return new ResponseEntity<>(new Message("네이버 로그인 성공", responseDto), HttpStatus.OK);
    }
}
