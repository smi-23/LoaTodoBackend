package com.loatodo.loatodobackend.domain.user.dto;

import com.loatodo.loatodobackend.util.UserRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {
    private Long id;
    private String username;
    private String email;
    private String name;
    private UserRole role;
}
