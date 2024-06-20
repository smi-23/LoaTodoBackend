package com.loatodo.loatodobackend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ALREADY_SIGNUP_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 username입니다."),
    NOT_USER(HttpStatus.BAD_REQUEST, "가입된 아이디가 아닙니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String data;
}