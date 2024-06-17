package com.loatodo.loatodobackend.domain.user.repository;

import com.loatodo.loatodobackend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(Long username);
    Optional<User> findByEmail(String email);
}
