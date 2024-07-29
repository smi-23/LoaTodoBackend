package com.loatodo.loatodobackend.domain.user.dto.update;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailDto {
    private String email;
    private String newEmail;
}
