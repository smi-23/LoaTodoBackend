package com.loatodo.loatodobackend.domain.user.dto.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//@ToString // 로그가 안찍힐 때 이유는?
public class GoogleProfile {
    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
}
