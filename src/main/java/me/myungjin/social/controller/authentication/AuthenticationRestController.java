package me.myungjin.social.controller.authentication;

import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.error.UnauthorizedException;
import me.myungjin.social.security.AuthenticationRequest;
import me.myungjin.social.security.AuthenticationResult;
import me.myungjin.social.security.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static me.myungjin.social.controller.ApiResult.OK;


@RestController
public class AuthenticationRestController {

  private final AuthenticationManager authenticationManager;


  public AuthenticationRestController(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("api/auth")
  public ApiResult<AuthenticationResult> authentication(@RequestBody AuthenticationRequest authRequest) throws UnauthorizedException {
    try {
      JwtAuthenticationToken authToken = new JwtAuthenticationToken(authRequest.getPrincipal(), authRequest.getCredentials());
      Authentication authentication = authenticationManager.authenticate(authToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return OK(
        (AuthenticationResult) authentication.getDetails()
      );
    } catch (AuthenticationException e) {
      throw new UnauthorizedException(e.getMessage());
    }
  }

  @GetMapping(path = "/login/oauth2/code/{registrationId}")
  public  ApiResult<Map<String, String>> socialAuthentication (@PathVariable String registrationId, @RequestParam String code, @RequestParam String state  ){
    Map<String, String> returnMap = new HashMap<>();
    returnMap.put("registrationId", registrationId);
    returnMap.put("code", code);
    returnMap.put("state", state);
    return OK(returnMap);
  }
}