package com.loatodo.loatodobackend.domain.user.dto;

import com.loatodo.loatodobackend.util.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "ID를 입력하세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "이름을 입력하세요.")
    private String name;

//    @NotBlank(message = "역할을 입력하세요.")
//    private UserRole role;

}
