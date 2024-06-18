package com.loatodo.loatodobackend.domain.user.service;

import com.loatodo.loatodobackend.config.auth.CustomOAuth2UserDetails;
import com.loatodo.loatodobackend.config.auth.OAuthAttributes;
import com.loatodo.loatodobackend.domain.user.entity.User;
import com.loatodo.loatodobackend.domain.user.repository.UserRepository;
import com.loatodo.loatodobackend.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("getAttributes : {}",oAuth2User.getAttributes());

        String oauthProvider = userRequest.getClientRegistration().getRegistrationId();

        //OAuth2 로그인 진행시 키가 되는 필드값 프라이머리키와 같은 값 네이버 카카오 지원 x
        String usernameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        //OAuth2UserService를 통해 가져온 데이터를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(oauthProvider, usernameAttributeName, oAuth2User.getAttributes());

        // 뒤에 진행할 다른 소셜 서비스 로그인을 위해 구분 => 구글
        if(oauthProvider.equals("google")){
            log.info("구글 로그인");
        }

        String username = oauthProvider + "_" + attributes.getProviderId();
        Optional<User> findUser = userRepository.findByUsername(username);
        User user;
        if (!findUser.isPresent()) {
            user = User.builder()
                    .username(username)
                    .email(attributes.getEmail())
                    .name(attributes.getName())
                    .provider(oauthProvider)
                    .providerId(attributes.getProviderId())
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        } else{
            user = findUser.get();
        }

        return new CustomOAuth2UserDetails(user, oAuth2User.getAttributes());
    }
}
