package me.myungjin.social.repository.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface NotiRepository {

    Noti save(Noti noti);

    void delete(Id<Noti, Long> notificationId);

    Optional<Noti> findById(Id<Noti, Long> notificationId, Id<User, Long> userId);

    List<Noti> findAll(Id<User, Long> postId);

}
