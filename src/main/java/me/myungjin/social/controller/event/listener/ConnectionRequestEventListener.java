package me.myungjin.social.controller.event.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.myungjin.social.controller.event.ConnectionRequestEvent;
import me.myungjin.social.error.NotNotifiedException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.notification.NotificationService;
import me.myungjin.social.service.user.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionRequestEventListener implements AutoCloseable {

    private Logger log = LoggerFactory.getLogger(ConnectionRequestEventListener.class);

    private EventBus eventBus;

    private final NotificationService notificationService;

    private final ConnectionService connectionService;

    public ConnectionRequestEventListener(EventBus eventBus, NotificationService notificationService, ConnectionService connectionService) {
        this.eventBus = eventBus;
        this.notificationService = notificationService;
        eventBus.register(this);
        this.connectionService = connectionService;
    }

    @Subscribe
    public void handleConnectionRequestEvent(ConnectionRequestEvent event) throws Exception {
        Id<User, Long> requestedUserId = event.getRequestedUserId();
        Id<User, Long> targetId = event.getTargetId();
        log.info("{} requested a new connection with {} !", requestedUserId, targetId);

        try {
            log.info("Try to send push for {}", event);
            PushMessage pushMessage = new PushMessage(
                    "new connection request!",
                    "user/connections/grant",
                    "Please check new connection request"
            );
            notificationService.save(new Noti(targetId, pushMessage.getMessage(), pushMessage.getClickTarget()));
            notificationService.notifyUser(targetId, pushMessage);
        } catch (Exception e) {
            log.error("Got error while handling event ConnectionRequestEvent " + event.toString(), e);
            throw new NotNotifiedException(ConnectionRequestEvent.class, e.getMessage(), event);
        }
    }

    @Override
    public void close() throws Exception {
        eventBus.unregister(this);
    }
}
