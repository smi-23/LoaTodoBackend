package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.service.Oauth2KakaoService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Oauth2KakaoContorller {
    private final Oauth2KakaoService kakaoService;

    @GetMapping("/login/kakao")
    public ResponseEntity<Message> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {

//        KakaoTokenDto kakaoTokenDto = kakaoService.getKakaoToken(code);
//        TokenDto tokenDto = kakaoService.loginWithKakao(kakaoTokenDto);

//        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
//                .accessToken(tokenDto.getAccessToken())
//                .refreshToken(tokenDto.getRefreshToken())
//                .roles(tokenDto.getGrantType())
//                .build();
        return kakaoService.loginWithKakao(code, response);
    }
}
