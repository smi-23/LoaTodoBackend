package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.service.KakaoLoginService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoLoginContorller {
    private final KakaoLoginService kakaoService;

    @GetMapping("/login/kakao")
    public ResponseEntity<Message> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        return kakaoService.loginWithKakao(code, response);
    }
}
