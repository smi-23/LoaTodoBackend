package com.loatodo.loatodobackend.domain.user.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoProfile {
    private Long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Getter
    @Setter
    public static class Properties {
        private String nickname;
    }

    @Getter
    @Setter
    public static class KakaoAccount {
        private boolean profile_nickname_needs_agreement;
        private boolean profile_image_needs_agreement;
        private Profile profile;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile {
        private String nickname;
        private boolean is_default_nickname;
    }
}
