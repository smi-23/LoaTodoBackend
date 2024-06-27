package com.loatodo.loatodobackend.domain.user.dto;

import com.loatodo.loatodobackend.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private Long id;
    private String username;
    private String email;
    private String name;
    private UserRole role;
}
