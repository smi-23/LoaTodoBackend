package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.domain.user.dto.LoginRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.util.PwEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean checkLoginIdDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    public void signup(SignupRequestDto signupRequestDto) {
        User userinfo = User.builder()
                .username(signupRequestDto.getUsername())
                .password(signupRequestDto.getPassword())
                .name(signupRequestDto.getName())
                .role(signupRequestDto.getRole())
                .build();
        userRepository.save(userinfo);
    }

    public User login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User userInfo = userOptional.get();

            if (PwEncoder.encoder.matches(password, userInfo.getPassword())) {
                return userInfo; // 비밀번호가 일치하는 경우 사용자 정보 반환
            } else {
                return null; // 비밀번호가 일치하지 않는 경우 null 반환
            }
        } else {
            return null; // 사용자가 존재하지 않는 경우 null 반환
        }
    }

        public User getLoginUserById(Long userId) {
        if (userId == null) return null;

        Optional<User> findMember = userRepository.findById(userId);
        return findMember.orElse(null);

    }
}
