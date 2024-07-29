package com.loatodo.loatodobackend.domain.user.dto.naver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverToken {

    private String access_token;
    private String refresh_token;
    private String token_type;
    private int expires_in;
}
