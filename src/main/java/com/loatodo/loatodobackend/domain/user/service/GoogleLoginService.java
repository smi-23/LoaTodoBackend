package com.loatodo.loatodobackend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loatodo.loatodobackend.domain.user.dto.LoginResponseDto;
import com.loatodo.loatodobackend.domain.user.dto.google.GoogleProfile;
import com.loatodo.loatodobackend.domain.user.dto.naver.NaverProfile;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import com.loatodo.loatodobackend.util.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleLoginService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    //    private final ObjectMapper objectMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String google_client_id;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String google_client_secret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String google_redirect_uri;

    public String getGoogleToken(String code) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com/token")
                .build();

        String googleTokenResponse = webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", google_client_id)
                        .with("client_secret", google_client_secret)
                        .with("redirect_uri", google_redirect_uri)
                        .with("code", code))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            // ObjectMapper를 사용하여 JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(googleTokenResponse);

            // "access_token" 필드의 값을 추출
            String googleToken = jsonNode.get("access_token").asText();
            return googleToken;
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Message> loginWithGoogle(String code, HttpServletResponse response) {
        String googleToken = getGoogleToken(code);

        WebClient webclient = WebClient.builder()
                .baseUrl("https://www.googleapis.com/oauth2/v2/userinfo")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String googleProfileResponse = webclient.get()
                .header("Authorization", "Bearer " + googleToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();
//        log.info("response = {}", googleProfileResponse);

        GoogleProfile googleProfile = null;
        try {
            googleProfile = objectMapper.readValue(googleProfileResponse, GoogleProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
//        log.info("googleProfile = {}", googleProfile);

        String username = "Google" + "_" + googleProfile.getId();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("이미 구글아이디로 회원정보가 등록되어있습니다. 로그인만 진행합니다.");
        } else {
            // 사용자가 없을 경우 새로 생성
            user = User.builder()
                    .username(username)
                    .name(googleProfile.getName())
                    .email(googleProfile.getEmail())
                    .provider("Google")
                    .providerId(String.valueOf(googleProfile.getId()))
                    .picture(googleProfile.getPicture())
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
            log.info("구글아이디로 회원가입이 완료되었습니다.");
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

        log.info("구글 로그인 되었습니다.");

        return new ResponseEntity<>(new Message("구글 로그인 성공", null), HttpStatus.OK);
    }
}
