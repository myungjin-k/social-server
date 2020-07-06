package me.myungjin.social.model.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class Subscription {

    private final Long seq;

    private final Id<User, Long> userId;

    private final String notificationEndPoint;

    private final String publicKey;

    private final String auth;

    private final LocalDateTime createAt;

    public Subscription(Id<User, Long> userId, String notificationEndPoint, String publicKey, String auth) {
        this(null, userId, notificationEndPoint, publicKey, auth, null);
    }

    public Subscription(Long seq, Id<User, Long> userId, String notificationEndPoint, String publicKey, String auth, LocalDateTime createAt) {
        checkNotNull(userId, "userId must be provided.");
        checkNotNull(notificationEndPoint, "notificationEndPoint must be provided.");
        checkNotNull(publicKey, "publicKey must be provided.");
        checkNotNull(auth, "auth must be provided.");
        this.seq = seq;
        this.userId = userId;
        this.notificationEndPoint = notificationEndPoint;
        this.publicKey = publicKey;
        this.auth = auth;
        this.createAt = defaultIfNull(createAt, now());
    }

    public Long getSeq() {
        return seq;
    }

    public Id<User, Long> getUserId() {
        return userId;
    }

    public String getNotificationEndPoint() {
        return notificationEndPoint;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAuth() {
        return auth;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("seq", seq)
                .append("userId", userId)
                .append("notificationEndPoint", notificationEndPoint)
                .append("publicKey", publicKey)
                .append("auth", auth)
                .append("createAt", createAt)
                .toString();
    }

    static public class Builder {
        private Long seq;
        private Id<User, Long> userId;
        private String notificationEndPoint;
        private String publicKey;
        private String auth;
        private LocalDateTime createAt;

        public Builder() {
        }

        public Builder(Subscription subscription){
            this.seq = subscription.seq;
            this.userId = subscription.userId;
            this.notificationEndPoint = subscription.notificationEndPoint;
            this.publicKey = subscription.publicKey;
            this.auth = subscription.auth;
            this.createAt = subscription.createAt;
        }

        public Builder seq(Long seq){
            this.seq = seq;
            return this;
        }
        public Builder userId(Id<User, Long> userId){
            this.userId = userId;
            return this;
        }
        public Builder notificationEndPoint(String notificationEndPoint){
            this.notificationEndPoint = notificationEndPoint;
            return this;
        }
        public Builder publicKey(String publicKey){
            this.publicKey = publicKey;
            return this;
        }
        public Builder auth(String auth){
            this.auth = auth;
            return this;
        }
        public Builder createAt(LocalDateTime createAt){
            this.createAt = createAt;
            return this;
        }

        public Subscription build() {
            return new Subscription(seq, userId, notificationEndPoint, publicKey, auth, createAt);
        }
    }

}