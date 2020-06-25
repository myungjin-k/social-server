package me.myungjin.social.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    public final Id<User, Long> id;

    public final String name;

    public final String email;

    @JsonIgnore
    public final String password;

    public final Role role;

    public UserPrincipal(Id<User, Long> id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Extract role
        GrantedAuthority authority = new SimpleGrantedAuthority(role.value());
        authorities.add(authority);
        return authorities;
    }

    public String newApiToken(Jwt jwt) {
        Jwt.Claims claims = Jwt.Claims.of(id.value(), name, email, new String[]{role.name()});
        return jwt.newToken(claims);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User toUser(){
        return new User(id.value(), name, email, password, role);
    }

    public static UserPrincipal of(User user){
        return new UserPrincipal(Id.of(User.class, user.getSeq()), user.getName(), user.getEmail(), user.getPassword(), user.getRole());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("name", name)
                .append("email", email)
                .append("role", role)
                .toString();
    }
}
