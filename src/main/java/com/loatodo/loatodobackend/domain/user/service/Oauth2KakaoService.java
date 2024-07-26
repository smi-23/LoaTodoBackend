package com.loatodo.loatodobackend.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loatodo.loatodobackend.domain.user.dto.kakao.KakaoToken;
import com.loatodo.loatodobackend.domain.user.dto.kakao.KakaoProfile;
import com.loatodo.loatodobackend.domain.user.dto.LoginResponseDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Oauth2KakaoService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.kakao.client_id}")
    String kakao_client_id;

    @Value("${spring.security.kakao.redirect_uri}")
    String kakao_redirect_uri;

    public KakaoToken getKakaoToken(String code) {
        //(2)
        RestTemplate rt = new RestTemplate();

        //(3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(4)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakao_client_id);
        params.add("redirect_uri", kakao_redirect_uri);
        params.add("code", code);
//        params.add("client_secret", "{시크릿 키}"); // 생략 가능!

        //(5)
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        //(6)
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //(7)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken kakaoToken = null;
        try {
            kakaoToken = objectMapper.readValue(accessTokenResponse.getBody(), KakaoToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // KakaoToken 객체의 toString 메서드를 사용하여 로그에 출력
//        log.info("KakaoToken: {}", kakaoToken);
        return kakaoToken;
    }

    public ResponseEntity<Message> loginWithKakao(String code, HttpServletResponse response) {
        KakaoToken kakaoToken = getKakaoToken(code);

        //(1-2)
        RestTemplate rt = new RestTemplate();

        //(1-3)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoToken.getAccess_token()); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //(1-5)
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        //(1-6)
        // Http 요청 (POST 방식) 후, response 변수에 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        //(1-7)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info("kakao profile: {}", kakaoProfile);

        String username = "Kakao" + "_" + kakaoProfile.getId();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            log.info("이미 카카오아이디로 회원정보가 등록되어있습니다. 로그인만 진행합니다.");
        } else {
            // 사용자가 없을 경우 새로 생성
            user = User.builder()
                    .username(username)
                    .name(kakaoProfile.getProperties().getNickname())
                    .provider("Kakao")
                    .providerId(String.valueOf(kakaoProfile.getId()))
//                    .picture(kakaoProfile..getProfile_image())
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
            log.info("카카오아이디로 회원가입이 완료되었습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        // jwtToken 헤더에 넣어주기
        response.addHeader("Authorization", accessToken);
        response.addHeader("RefreshToken", refreshToken);

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

        log.info("로그인 되었습니다.");

        return new ResponseEntity<>(new Message("카카오 로그인 성공", responseDto), HttpStatus.OK);
    }
}
