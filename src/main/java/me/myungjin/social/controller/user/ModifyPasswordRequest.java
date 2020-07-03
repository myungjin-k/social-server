package me.myungjin.social.controller.user;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ModifyPasswordRequest {

    private String oldPassword;

    private String password;


    protected ModifyPasswordRequest() {}

    public String getOldPassword() {
        return oldPassword;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("oldPassword", oldPassword)
                .append("password", password)
                .toString();
    }
}
