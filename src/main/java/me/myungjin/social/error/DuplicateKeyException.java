package me.myungjin.social.error;

import me.myungjin.social.util.MessageUtils;
import org.apache.commons.lang3.StringUtils;

public class DuplicateKeyException extends ServiceRuntimeException {

  static final String MESSAGE_KEY = "error.duplicatekey";

  static final String MESSAGE_DETAILS = "error.duplicatekey.details";

  public DuplicateKeyException(Class cls, Object... values) {
    this(cls.getSimpleName(), values);
  }

  public DuplicateKeyException(String targetName, Object... values) {
    super(MESSAGE_KEY, MESSAGE_DETAILS, new String[]{targetName, (values != null && values.length > 0) ? StringUtils.join(values, ",") : ""});
  }

  @Override
  public String getMessage() {
    return MessageUtils.getMessage(getDetailKey(), getParams());
  }

  @Override
  public String toString() {
    return MessageUtils.getMessage(getMessageKey());
  }

}