package me.myungjin.social.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.subscription.SubscriptionRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final PushService pushService;

    private final SubscriptionRepository subscriptionRepository;

    private final ObjectMapper objectMapper;

    public NotificationService(ObjectMapper objectMapper, PushService pushService, SubscriptionRepository subscriptionRepository) throws Exception {
        this.objectMapper = objectMapper;
        this.pushService = pushService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Subscription subscribe(Subscription newSubscription) {
        return subscriptionRepository.findByUserId(newSubscription.getUserId())
                .map(subscription -> {
                    subscriptionRepository.update(subscription);
                    return subscription;
                }).orElseGet(() -> save(newSubscription));
    }

    @Transactional
    public PushMessage notifyUser(Id<User, Long> userId, PushMessage message) throws Exception {
       sendNotification(findByUserId(userId).orElseThrow(() -> new NotFoundException(Subscription.class, userId)),
                         message);
       return message;
    }

    @Transactional
    public PushMessage notifyAll(PushMessage message) throws Exception {
        for (Subscription subscription : findAll() ) {
            sendNotification(subscription, message);
        }
        return message;
    }

    @Transactional(readOnly = true)
    public Optional<Subscription> findByUserId(Id<User, Long> userId){
        return subscriptionRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Subscription> findAll(){
        return subscriptionRepository.findAll();
    }

    private void sendNotification(Subscription subscription, PushMessage message) throws Exception {
        pushService.send(new Notification(subscription.getNotificationEndPoint(),
                subscription.getPublicKey(),
                subscription.getAuth(),
                objectMapper.writeValueAsBytes(message)));

    }

    private Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }
}