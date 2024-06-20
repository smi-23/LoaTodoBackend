package com.loatodo.loatodobackend.domain.user.dto;

import com.loatodo.loatodobackend.util.UserRole;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponseDto {
    String username;
    String email;
    String name;
    UserRole role;
}
