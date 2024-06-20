package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.domain.user.dto.LoginRequestDto;
import com.loatodo.loatodobackend.domain.user.dto.SignupRequestDto;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.util.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService_temp {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean checkLoginIdDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    public void signup(SignupRequestDto signupRequestDto) {
        User userinfo = User.builder()
                .username(signupRequestDto.getUsername())
                .password(signupRequestDto.getPassword())
                .name(signupRequestDto.getName())
                .role(UserRole.USER)
                .build();
        userRepository.save(userinfo);
    }

    public void securitySignup(SignupRequestDto signupRequestDto) {
        if (userRepository.existsByUsername(signupRequestDto.getUsername())) {
            return;
        }

        signupRequestDto.setPassword(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()));

        User userinfo = User.builder()
                .username(signupRequestDto.getUsername())
                .password(signupRequestDto.getPassword())
                .name(signupRequestDto.getName())
                .role(UserRole.USER)
                .build();
//        userRepository.save(signupRequestDto.toEntity());
        userRepository.save(userinfo);
    }

    public User login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User userInfo = userOptional.get();

            if (bCryptPasswordEncoder.matches(password, userInfo.getPassword())) {
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

    public User getLoginUserByUsername(String username) {
        if (username == null) {
            return null; // 또는 적절한 예외를 던질 수 있습니다.
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

}
