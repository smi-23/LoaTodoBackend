package com.loatodo.loatodobackend.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDto {
    private String currentPassword;
    private String newPassword;
    private String newPasswordCheck;
    private String email;
    private String name;
}
