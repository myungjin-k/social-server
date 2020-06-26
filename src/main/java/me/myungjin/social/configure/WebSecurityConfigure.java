package me.myungjin.social.configure;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.*;
import me.myungjin.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.math.NumberUtils.toLong;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

  private final Jwt jwt;

  private final JwtTokenConfigure jwtTokenConfigure;

  private final JwtAccessDeniedHandler accessDeniedHandler;

  private final EntryPointUnauthorizedHandler unauthorizedHandler;

  public WebSecurityConfigure(Jwt jwt, JwtTokenConfigure jwtTokenConfigure, JwtAccessDeniedHandler accessDeniedHandler, EntryPointUnauthorizedHandler unauthorizedHandler) {
    this.jwt = jwt;
    this.jwtTokenConfigure = jwtTokenConfigure;
    this.accessDeniedHandler = accessDeniedHandler;
    this.unauthorizedHandler = unauthorizedHandler;
  }

  @Bean
  public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
    return new JwtAuthenticationTokenFilter(jwtTokenConfigure.getHeader(), jwt);
  }

  @Override
  public void configure(WebSecurity web) {
    web.ignoring().antMatchers("/swagger-resources", "/webjars/**", "/static/**", "/templates/**", "/h2/**");
  }

  @Autowired
  public void configureAuthentication(AuthenticationManagerBuilder builder, JwtAuthenticationProvider authenticationProvider) {
    builder.authenticationProvider(authenticationProvider);
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider(Jwt jwt, UserService userService) {
    return new JwtAuthenticationProvider(jwt, userService);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ConnectionBasedVoter connectionBasedVoter() {
    final String regex = "^/api/user/(\\d+)/post/.*$";
    Pattern pattern = Pattern.compile(regex);
    RequestMatcher requiresAuthorizationRequestMatcher = new RegexRequestMatcher(pattern.pattern(), null);
    return new ConnectionBasedVoter(
      requiresAuthorizationRequestMatcher,
      (String url) -> {
        /* url에서 targetId를 추출하기 위해 정규식 처리 */
        Matcher matcher = pattern.matcher(url);
        long id = matcher.find() ? toLong(matcher.group(1), -1) : -1;
        return Id.of(User.class, id);
      }
    );
  }

  @Bean
  public AccessDecisionManager accessDecisionManager() {
    List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
    decisionVoters.add(new WebExpressionVoter());
    decisionVoters.add(connectionBasedVoter());
    // 모든 voter가 승인해야 해야한다.
    return new UnanimousBased(decisionVoters);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      // CSRF(Cross Site Request Forgery : 사이트 간 요청 위조) protection 해제
      // 개발 옵션
      .csrf()
        .disable()
      // 개발 옵션
      .headers()
        .disable()
      .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler)
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
       // No session will be created or used by spring security
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
      .authorizeRequests()
        .antMatchers("/api/auth").permitAll()
        .antMatchers("/login/oauth2/code/*").permitAll()
        .antMatchers("/api/user/join").permitAll()
        .antMatchers("/api/users").hasRole(Role.ADMIN.name())
        .antMatchers("/api/**").authenticated()
        .accessDecisionManager(accessDecisionManager())
        .anyRequest().permitAll()
        .and()
       .oauth2Login()
        .successHandler(new MyOAuth2SuccessHandler())
        .and()
      .formLogin()
        .disable();
    http
      .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
  public class MyOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      String id = authentication.getName();

      LinkedHashMap<String, Object> properties = (LinkedHashMap<String, Object>) ((DefaultOAuth2User)authentication.getPrincipal()).getAttributes().get("properties");
      LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) ((DefaultOAuth2User)authentication.getPrincipal()).getAttributes().get("kakao_account");

      String name = (String) properties.get("nickname");
      String email = (String) kakaoAccount.get("email");
      Jwt.Claims claims = Jwt.Claims.of(Integer.parseInt(id), name, email, new String[]{Role.USER.value()});
      String token = jwt.newToken(claims);

      response.addHeader("Authorization", "Bearer " + token);
    }
  }
}
