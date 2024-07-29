package com.loatodo.loatodobackend.domain.user.dto.naver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverProfile {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @Setter
//    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private String id;
        private String email;
        private String name;
    }
}