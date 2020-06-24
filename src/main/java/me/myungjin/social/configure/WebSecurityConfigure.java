package me.myungjin.social.configure;

import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.*;
import me.myungjin.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigure extends WebSecurityConfigurerAdapter {

  private final Jwt jwt;

  private final JwtTokenConfigure jwtTokenConfigure;

  private final UserPrincipalDetailsService userDetailService;

  private final JwtAccessDeniedHandler accessDeniedHandler;

  private final EntryPointUnauthorizedHandler unauthorizedHandler;


  public WebSecurityConfigure(Jwt jwt, JwtTokenConfigure jwtTokenConfigure, UserPrincipalDetailsService userDetailService, JwtAccessDeniedHandler accessDeniedHandler, EntryPointUnauthorizedHandler unauthorizedHandler) {
    this.jwt = jwt;
    this.jwtTokenConfigure = jwtTokenConfigure;
    this.userDetailService = userDetailService;
    this.accessDeniedHandler = accessDeniedHandler;
    this.unauthorizedHandler = unauthorizedHandler;
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
  public JwtAuthenticationProvider jwtAuthenticationProvider() {
    return new JwtAuthenticationProvider(jwt, userDetailService);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
  @Bean
  public JwtAuthenticationFilter jwtAuthorizationFilter(){
    return new JwtAuthenticationFilter(jwtTokenConfigure.getHeader(), jwt);
  }

  @Bean
  PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
            // remove csrf and state in session because in jwt we do not need them
            .csrf().disable()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(unauthorizedHandler)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
           //.addFilter(new JwtAuthenticationFilter(authenticationManager(),  this.userRepository))
            .authorizeRequests()
            // configure access rules
            .antMatchers("/api/auth").permitAll()
            .antMatchers("/api/user/join").permitAll()
            .antMatchers("/api/users").hasRole(Role.ADMIN.name())
            .antMatchers("/api/**").hasRole(Role.USER.name())
            .anyRequest().permitAll();

    http
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}