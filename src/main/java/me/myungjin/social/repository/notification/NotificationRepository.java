package me.myungjin.social.repository.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Notification;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    void delete(Id<Notification, Long> notificationId);

    Optional<Notification> findById(Id<Notification, Long> notificationId, Id<User, Long> userId);

    List<Notification> findAll(Id<User, Long> postId);

}
