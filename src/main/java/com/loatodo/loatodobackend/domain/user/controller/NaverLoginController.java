package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.service.NaverLoginService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NaverLoginController {
    private final NaverLoginService naverLoginService;

    @GetMapping("/login/naver")
    public ResponseEntity<Message> naverCallback(@RequestParam("code") String code,HttpServletResponse response) {
        return naverLoginService.loginWithNaver(code, response);
    }
}