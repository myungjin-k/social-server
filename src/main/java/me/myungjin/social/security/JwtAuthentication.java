package me.myungjin.social.security;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.User;

import static com.google.common.base.Preconditions.checkNotNull;

public class JwtAuthentication {

  public final Id<User, Long> id;

  public final String name;

  public final String email;

  JwtAuthentication(Long id, String name, String email) {
    checkNotNull(id, "id must be provided.");
    checkNotNull(name, "name must be provided.");
    checkNotNull(email, "email must be provided.");

    this.id = Id.of(User.class, id);
    this.name = name;
    this.email = email;
  }

}