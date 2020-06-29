package me.myungjin.social.model.post;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

public class Writer {

  private final String String;

  private final String name;

  public Writer(String String) {
    this(String, null);
  }

  public Writer(String String, String name) {
    checkNotNull(String, "String must be provided.");

    this.String = String;
    this.name = name;
  }

  public String getString() {
    return String;
  }

  public Optional<String> getName() {
    return ofNullable(name);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("String", String)
      .append("name", name)
      .toString();
  }

}