package com.loatodo.loatodobackend.domain.user.repository;

import com.loatodo.loatodobackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // 로그인 ID를 갖는 객체가 존재하는지 => 존재하면 true 리턴 (ID 중복 검사 시 필요)
    boolean existsByUsername(String username);
}
