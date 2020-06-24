package me.myungjin.social.configure;

import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.Jwt;
import me.myungjin.social.security.JwtAuthenticationFilter;
import me.myungjin.social.security.JwtAuthenticationProvider;
import me.myungjin.social.security.UserPrincipalDetailsService;
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


  public WebSecurityConfigure(Jwt jwt, JwtTokenConfigure jwtTokenConfigure, UserPrincipalDetailsService userDetailService, UserService userService) {
    this.jwt = jwt;
    this.jwtTokenConfigure = jwtTokenConfigure;
    this.userDetailService = userDetailService;
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
/*

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationTokenFilter() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwt, jwtTokenConfigure.getHeader(), authenticationManagerBean());
    filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/**"));
    return filter;
  }
*/

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