package me.myungjin.social.repository.subscription;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findByUserId(Id<User, Long> userId);

    List<Subscription> findAll();

    void update(Subscription subscription);
}
