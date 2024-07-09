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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("user/api")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public String index() {
        return "index";
    }

    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@RequestBody @Valid SignupRequestDto reqeustDto) {
        return userService.signup(reqeustDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

    @PostMapping("/update/email")
    public ResponseEntity<Message> updateEmail(@RequestBody @Valid UpdateEmailDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.updateEmail(requestDto, request, response);
    }

    @PostMapping("/update/name")
    public ResponseEntity<Message> updateName(@RequestBody @Valid UpdateNameDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.updateName(requestDto, request, response);
    }

    @PostMapping("/update/password")
    public ResponseEntity<Message> updatePassword(@RequestBody @Valid UpdatePasswordDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.updatePassword(requestDto, request, response);
    }

    @PostMapping("/refresh-tokens")
    public ResponseEntity<Message> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        return userService.refreshTokens(request, response);
    }

}
