package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.service.GoogleLoginService;
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
public class GoogleLoginController {
    private final GoogleLoginService googleService;

    @GetMapping("/login/google")
    public ResponseEntity<Message> googleCallback(@RequestParam("code") String code, HttpServletResponse response) {
        return googleService.loginWithGoogle(code, response);
    }
}
