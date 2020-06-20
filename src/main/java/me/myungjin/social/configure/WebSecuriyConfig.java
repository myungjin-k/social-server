package me.myungjin.social.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecuriyConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            // user와 password 정보를 h2와 같은 in memory에 저장
            .inMemoryAuthentication()
                .withUser("myungjin_admin").password(passwordEncoder().encode("myungjin_admin")).roles("ADMIN")
                .and()
                .withUser("myungjin").password(passwordEncoder().encode("myungjin")).roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            // 모든 http request는 인증된 사용자만 접근할 수 있도록
            .anyRequest().authenticated()
            .and()
            // 사용자 인증 방법으로는 HTTP Basic Authentication을 사용
            .httpBasic();
    }
}
