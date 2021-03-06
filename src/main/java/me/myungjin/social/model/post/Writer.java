package me.myungjin.social.model.post;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

public class Writer {

  private final String email;

  private final String name;

  public Writer(String email) {
    this(email, null);
  }

  public Writer(String email, String name) {
    checkNotNull(email, "email must be provided.");

    this.email = email;
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public Optional<String> getName() {
    return ofNullable(name);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("email", email)
      .append("name", name)
      .toString();
  }

}