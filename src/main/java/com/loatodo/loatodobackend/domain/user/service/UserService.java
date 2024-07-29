package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.domain.user.dto.*;
import com.loatodo.loatodobackend.domain.user.dto.update.UpdateEmailDto;
import com.loatodo.loatodobackend.domain.user.dto.update.UpdateNameDto;
import com.loatodo.loatodobackend.domain.user.dto.update.UpdatePasswordDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.jwt.JwtUtil;
import com.loatodo.loatodobackend.util.Message;
import com.loatodo.loatodobackend.util.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwEncoder;
    private final JwtUtil jwtUtil;

    public ResponseEntity<Message> signup(SignupRequestDto requestDto) {
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();

        checkUsernameDuplicate(requestDto.getUsername());
        String encodedPassword = passwordEncodingAndCheck(password, passwordCheck);

        User user = User.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        SignupResponseDto responseDto = SignupResponseDto.builder()
                .id(user.getId())
                .username(requestDto.getUsername())
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .role(UserRole.USER)
                .build();

        return new ResponseEntity<>(new Message("회원 가입 성공", responseDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = findUser(requestDto.getUsername());

        String password = requestDto.getPassword();
        String encodedPassword = user.getPassword();
        if (!pwEncoder.matches(password, encodedPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());
        // jwtToken 헤더에 넣어주기
        response.addHeader("Authorization", accessToken);
        response.addHeader("RefreshToken", refreshToken);

        LoginResponseDto responseDto = LoginResponseDto.builder()
                .id(user.getId())
                .username(requestDto.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        log.info("로그인 되었습니다.");

        return new ResponseEntity<>(new Message("로그인 성공", responseDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> updateEmail(UpdateEmailDto Dto, HttpServletRequest request) {
        Claims claim = jwtUtil.getClaims(request);
        User user = findUser(claim.getSubject());
        String currentEmail = user.getEmail();

        user.updateEmail(Dto.getEmail());

        userRepository.save(user);

        UpdateEmailDto responseDto = UpdateEmailDto.builder()
                .email(currentEmail)
                .newEmail(Dto.getEmail())
                .build();

        return new ResponseEntity<>(new Message("회원 이메일 수정 성공", responseDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> updateName(UpdateNameDto Dto, HttpServletRequest request) {
        Claims claim = jwtUtil.getClaims(request);
        User user = findUser(claim.getSubject());
        String currentName = user.getName();

        user.updateName(Dto.getName());

        userRepository.save(user);

        UpdateNameDto responseDto = UpdateNameDto.builder()
                .name(currentName)
                .newName(Dto.getName())
                .build();

        return new ResponseEntity<>(new Message("회원 이름 수정 성공", responseDto), HttpStatus.OK);
    }

    public ResponseEntity<Message> updatePassword(UpdatePasswordDto Dto, HttpServletRequest request) {
        Claims claim = jwtUtil.getClaims(request);
        User user = findUser(claim.getSubject());

        String currentPassword = user.getPassword();
        String newPassword = Dto.getNewPassword();
        String newPasswordCheck = Dto.getNewPasswordCheck();

        // 현재 비밀번호를 통해 2차검증
        pwEncoder.matches(currentPassword, newPassword);

        // 우선 입력된 비밀번호가 일치하는지 확인 후 인코딩
        String updatePassword = passwordEncodingAndCheck(newPassword, newPasswordCheck);

        user.updatePassword(updatePassword);

        userRepository.save(user);

        return new ResponseEntity<>(new Message("회원 비밀번호 수정 성공", null), HttpStatus.OK);
    }

    public ResponseEntity<Message> refreshTokens(HttpServletRequest request, HttpServletResponse response) {
        // 유저 정보를 우선 찾고
        String accessToken = jwtUtil.resolveToken(request, "Authorization");
        Claims claim = jwtUtil.getUserInfoFromToken(accessToken);
        User user = findUser(claim.getSubject());

        // 리프레시토큰 검증
        jwtUtil.refreshTokens(user,request, response);
        return new ResponseEntity<>(new Message("토큰 재발급 성공", null), HttpStatus.OK);
    }

    public ResponseEntity<Message> checkUsername(String username) {
        checkUsernameDuplicate(username);
        return ResponseEntity.ok(new Message("사용 가능한 유저네임입니다.", null));
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
