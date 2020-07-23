package me.myungjin.social.controller.event;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ConnectionGrantEvent {

    private final Id<User, Long> requestedUserId;

    private final Id<User, Long> targetId;

    public ConnectionGrantEvent(Id<User, Long> requestedUserId, Id<User, Long> targetId) {
        this.requestedUserId = requestedUserId;
        this.targetId = targetId;
    }

    public Id<User, Long> getRequestedUserId() {
        return requestedUserId;
    }

    public Id<User, Long> getTargetId() {
        return targetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("requestedUserId", requestedUserId)
                .append("targetId", targetId)
                .toString();
    }
}
