package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.SignupResponseDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.exception.CustomException;
import com.loatodo.loatodobackend.exception.ErrorCode;
import com.loatodo.loatodobackend.util.Message;
import com.loatodo.loatodobackend.util.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder pwEncoder;

    public ResponseEntity<Message> signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();

        // 비밀번호와 비밀번호 확인 값이 일치하는지 확인
        // 비밀번호가 메모리에 저장되어 있었을텐데 이게 유출될 가능성은?
        if (!password.equals(passwordCheck)) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 비밀번호를 암호화하여 저장
        String encodedPassword = pwEncoder.encode(password);

        usernameDuplicateCheck(requestDto);

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .email(requestDto.getEmail())
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        SignupResponseDto responseDto = SignupResponseDto.builder()
                .username(requestDto.getUsername())
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .role(UserRole.USER)
                .build();

        return new ResponseEntity<>(new Message("회원 가입 성공", responseDto), HttpStatus.OK);
    }

    public void usernameDuplicateCheck(SignupRequestDto requestDto) {
        Optional<User> user = userRepository.findByUsername(requestDto.getUsername());
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_SIGNUP_USER);
        }
    }

}
