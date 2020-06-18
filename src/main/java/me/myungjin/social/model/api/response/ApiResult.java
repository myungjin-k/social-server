package me.myungjin.social.model.api.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ApiResult<T> {

    private final boolean success;

    private final T response;

    public ApiResult(boolean success, T response) {
        this.success = success;
        this.response = response;
    }

    public static <T> ApiResult<T> OK(T response) {
        return new ApiResult<>(true, response);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("success", success)
                .append("response", response)
                .toString();
    }
}
