package me.myungjin.social.configure;

import me.myungjin.social.security.CustomOAuthProvider;
import me.myungjin.social.security.MyOAuth2AuthorizedClientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.Collections;

@Configuration
public class MyOAuth2Configuration {

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(MyOAuth2AuthorizedClientService myOAuth2AuthorizedClientService) {
        return myOAuth2AuthorizedClientService;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        final ClientRegistration clientRegistration = CustomOAuthProvider.KAKAO
                .getBuilder()
                .build();

        return new InMemoryClientRegistrationRepository(Collections.singletonList(
                clientRegistration
        ));
    }

}