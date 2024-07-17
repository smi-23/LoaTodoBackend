package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.dto.LoginRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.update.UpdateEmailDto;
import com.loatodo.loatodobackend.domain.user.dto.update.UpdateNameDto;
import com.loatodo.loatodobackend.domain.user.dto.update.UpdatePasswordDto;
import com.loatodo.loatodobackend.domain.user.service.UserService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@RequestBody @Valid SignupRequestDto reqeustDto) {
        return userService.signup(reqeustDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @PostMapping("/update/email")
    public ResponseEntity<Message> updateEmail(@RequestBody @Valid UpdateEmailDto requestDto, HttpServletRequest request) {
        return userService.updateEmail(requestDto, request);
    }

    @PostMapping("/update/name")
    public ResponseEntity<Message> updateName(@RequestBody @Valid UpdateNameDto requestDto, HttpServletRequest request) {
        return userService.updateName(requestDto, request);
    }

    @PostMapping("/update/password")
    public ResponseEntity<Message> updatePassword(@RequestBody @Valid UpdatePasswordDto requestDto, HttpServletRequest request) {
        return userService.updatePassword(requestDto, request);
    }


    @PostMapping("/refresh-tokens")
    public ResponseEntity<Message> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        String accessToken =  request.getHeader("Authorization");
        String refreshToken =  request.getHeader("RefreshToken");
        log.info(accessToken);
        log.info(refreshToken);

        return userService.refreshTokens(request, response);
    }

    @PostMapping("/check-username")
    public ResponseEntity<Message> checkUsername(@RequestBody Map<String, String> usernameMap) {
        String username = usernameMap.get("username");
        return userService.checkUsername(username);
    }

}
