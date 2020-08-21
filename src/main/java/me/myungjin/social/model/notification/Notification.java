package me.myungjin.social.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class Notification {

    @JsonIgnore
    private final Long seq;

    private final Id<User, Long> userId;

    private final String message;

    private final String clickTarget;

    private final LocalDateTime createAt;

    public Notification(Id<User, Long> userId, String message, String clickTarget) {
        this(null, userId, message, clickTarget, null);
    }

    public Notification(Long seq, Id<User, Long> userId, String message, String clickTarget, LocalDateTime createAt) {
        checkNotNull(userId, "userId must be provided.");
        checkNotNull(message, "message must be provided.");
        this.seq = seq;
        this.userId = userId;
        this.message = message;
        this.clickTarget = clickTarget;
        this.createAt = defaultIfNull(createAt, now());
    }

    public Long getSeq() {
        return seq;
    }

    public Id<User, Long> getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getClickTarget() {
        return clickTarget;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("seq", seq)
                .append("userId", userId)
                .append("message", message)
                .append("clickTarget", clickTarget)
                .append("createAt", createAt)
                .toString();
    }

    static public class Builder {
        private Long seq;
        private Id<User, Long> userId;
        private String message;
        private String clickTarget;
        private LocalDateTime createAt;

        public Builder() {
        }

        public Builder(Notification notification) {
            this.seq = notification.seq;
            this.userId = notification.userId;
            this.message = notification.message;
            this.clickTarget = notification.clickTarget;
            this.createAt = notification.createAt;
        }
        public Builder seq(Long seq){
            this.seq = seq;
            return this;
        }

        public Builder userId(Id<User, Long> userId){
            this.userId = userId;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder clickTarget(String clickTarget){
            this.clickTarget = clickTarget;
            return this;
        }

        public Builder createAt(LocalDateTime createAt){
            this.createAt = createAt;
            return this;
        }

        public Notification build() {
            return new Notification(seq, userId, message, clickTarget, createAt);
        }
    }
}
