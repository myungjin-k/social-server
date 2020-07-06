package me.myungjin.social.controller.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SubscribeRequest {

    private String notificationEndPoint;

    private String publicKey;

    private String auth;

    protected SubscribeRequest() {}

    public String getNotificationEndPoint() {
        return notificationEndPoint;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAuth() {
        return auth;
    }

    public Subscription newSubscription(Id<User, Long> userId){
        return new Subscription(userId, notificationEndPoint, publicKey, auth);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("notificationEndPoint", notificationEndPoint)
                .append("publicKey", publicKey)
                .append("auth", auth)
                .toString();
    }
}
