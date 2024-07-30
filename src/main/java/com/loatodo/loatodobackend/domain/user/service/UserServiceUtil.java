package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceUtil {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwEncoder;
    private final JwtUtil jwtUtil;


    public User getUser(HttpServletRequest request){
        Claims claims = jwtUtil.getClaims(request);
        String username = claims.getSubject();
        return findUser(username);
    }

    public User findUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return user.get();
    }

    public void checkUsernameDuplicate(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_SIGNUP_USER);
        }
    }

    public String passwordEncodingAndCheck(String password, String passwordCheck) {
        checkPasswordMatch(password, passwordCheck);
        return pwEncoder.encode(password);
    }

    private void checkPasswordMatch(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

}
