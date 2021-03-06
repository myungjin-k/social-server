package me.myungjin.social.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.notification.NotiRepository;
import me.myungjin.social.repository.subscription.SubscriptionRepository;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class NotificationService {

    private final PushService pushService;

    private final SubscriptionRepository subscriptionRepository;

    private final NotiRepository notificationRepository;

    private final ObjectMapper objectMapper;

    public NotificationService(ObjectMapper objectMapper, PushService pushService,
                               SubscriptionRepository subscriptionRepository, NotiRepository notificationRepository) throws Exception {
        this.objectMapper = objectMapper;
        this.pushService = pushService;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
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
      // save(new Noti(userId, message.getMessage(), message.getClickTarget()));
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

    @Transactional
    public Noti remove(Id<Noti, Long> notiId, Id<User, Long> userId){
        return findByUserId(notiId, userId)
                .map(noti -> {
                    notificationRepository.delete(notiId);
                    return noti;
                }).orElseThrow(() -> new NotFoundException(Noti.class, notiId, userId));
    }

    @Transactional(readOnly = true)
    public Optional<Noti> findByUserId(Id<Noti, Long> notiId, Id<User, Long> userId){
        checkNotNull(notiId, "notiId must be provided.");
        return notificationRepository.findById(notiId, userId);
    }

    @Transactional(readOnly = true)
    public List<Noti> findAll(Id<User, Long> userId){
        checkNotNull(userId, "userId must be provided.");
        return notificationRepository.findAll(userId);
    }

    private Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Noti save(Noti noti) {
        return notificationRepository.save(noti);
    }
}