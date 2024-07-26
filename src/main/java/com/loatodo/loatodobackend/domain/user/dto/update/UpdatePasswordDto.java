package com.loatodo.loatodobackend.domain.user.dto.update;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto {
    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String currentPassword;
    @NotBlank(message = "새로운 비밀번호를 입력하세요.")
    private String newPassword;
    @NotBlank(message = "새로운 비밀번호 확인을 입력하세요.")
    private String newPasswordCheck;
}

