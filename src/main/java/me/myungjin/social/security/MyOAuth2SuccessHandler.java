package me.myungjin.social.security;


import me.myungjin.social.configure.JwtTokenConfigure;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

@Component
public class MyOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    private final Jwt jwt;

    private final JwtTokenConfigure jwtTokenConfigure;

    public MyOAuth2SuccessHandler(UserRepository userRepository, Jwt jwt, JwtTokenConfigure jwtTokenConfigure) {
        this.userRepository = userRepository;
        this.jwt = jwt;
        this.jwtTokenConfigure = jwtTokenConfigure;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) ((DefaultOAuth2User)authentication.getPrincipal()).getAttributes().get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(User.class, email));
        String token = user.newApiToken(jwt, new String[]{Role.USER.value()});

        response.addHeader(jwtTokenConfigure.getHeader(), "Bearer " + token);

    }


}