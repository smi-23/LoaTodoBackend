package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.SignupResponseDto;
import com.loatodo.loatodobackend.domain.user.dto.UpdateUserDto;
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

    public ResponseEntity<Message> updateUserInfo(UpdateUserDto updateUserDto, Long userId) {
        // userId를 받아오거나 로그인을 구현하면서 jwt토큰을 통해서 유저가 존재하는지 확인하도록 변경할 필요가 있음

        // 유저를 찾는 메서드 호출
        Optional<User> user = findUser(userId);
        User updateUser = user.get();

        String currentPassword = updateUserDto.getCurrentPassword();
        String newPassword = updateUserDto.getNewPassword();
        String newPasswordCheck = updateUserDto.getNewPasswordCheck();

        if (!pwEncoder.matches(currentPassword, updateUser.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        checkPasswordMatch(currentPassword, newPassword);
        String newEncodedPassword = passwordEncodingAndCheck(newPassword, newPasswordCheck);
        String newName = updateUserDto.getName();
        String newEmail = updateUserDto.getEmail();

        updateUser.updateUserInfo(newEncodedPassword, newName, newEmail);

        userRepository.save(updateUser);

        SignupResponseDto responseDto = SignupResponseDto.builder()
                .id(updateUser.getId())
                .username(updateUser.getUsername())
                .name(updateUser.getName())
                .email(updateUser.getEmail())
                .role(updateUser.getRole())
                .build();

        return new ResponseEntity<>(new Message("회원 정보 수정 성공", responseDto), HttpStatus.OK);
    }

    public Optional<User> findUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
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
