package me.myungjin.social.security;


import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedHashMap;

@Service
public class MyOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private final UserRepository userRepository;

    public MyOAuth2AuthorizedClientService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        throw new NotImplementedException();
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication authentication) {
        String providerType = authorizedClient.getClientRegistration().getRegistrationId();
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String account = (String) ((LinkedHashMap) oauth2User.getAttribute("kakao_account")).get("email");
        String name = (String) ((LinkedHashMap) ((LinkedHashMap) oauth2User.getAttribute("kakao_account")).get("profile")).get("nickname");

        User user = new User(name, account, accessToken.getTokenValue());
        if(userRepository.findByEmail(account).isPresent()){
            userRepository.update(user);
        } else {
            userRepository.save(user);
        }
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        throw new NotImplementedException();
    }
}