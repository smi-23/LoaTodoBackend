package com.loatodo.loatodobackend.domain.user.dto.update;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto {
    private String currentPassword;
    private String newPassword;
    private String newPasswordCheck;
}

