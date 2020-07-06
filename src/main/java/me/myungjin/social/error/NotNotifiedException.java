package me.myungjin.social.error;

import me.myungjin.social.util.MessageUtils;
import org.apache.commons.lang3.StringUtils;

public class NotNotifiedException extends ServiceRuntimeException {
    static final String MESSAGE_KEY = "error.notification";

    static final String MESSAGE_DETAILS = "error.notification.details";

    public NotNotifiedException(Class<?> cls, String cause, Object... values) {
        this(cls.getSimpleName(), cause, values);
    }

    public NotNotifiedException(String targetName, String cause, Object... values) {
        super(MESSAGE_KEY, MESSAGE_DETAILS, new String[]{targetName, (values != null && values.length > 0) ? StringUtils.join(values, ",") : "", cause});
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
