package com.loatodo.loatodobackend.domain.user.controller;

import com.loatodo.loatodobackend.domain.user.dto.LoginRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.UpdateUserDto;
import com.loatodo.loatodobackend.domain.user.service.UserService;
import com.loatodo.loatodobackend.util.Message;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("general")
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

    @PostMapping("/signup/update/{userId}")
    public ResponseEntity<Message> updateUserInfo(@RequestBody @Valid UpdateUserDto requestDto, @PathVariable Long userId) {
        return userService.updateUserInfo(requestDto, userId);
    }

    public ResponseEntity<Message> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {
        return userService.login(requestDto, response);
    }

}
