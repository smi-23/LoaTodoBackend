package com.loatodo.loatodobackend.domain.user.dto.update;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNameDto {
    private String name;
    private String newName;
}
