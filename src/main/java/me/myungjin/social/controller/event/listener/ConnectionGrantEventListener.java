package me.myungjin.social.controller.event.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.myungjin.social.controller.event.ConnectionGrantEvent;
import me.myungjin.social.controller.event.ConnectionRequestEvent;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.error.NotNotifiedException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.notification.NotificationService;
import me.myungjin.social.service.user.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionGrantEventListener implements AutoCloseable {

    private Logger log = LoggerFactory.getLogger(ConnectionGrantEventListener.class);

    private EventBus eventBus;

    private final NotificationService notificationService;

    private final ConnectionService connectionService;

    public ConnectionGrantEventListener(EventBus eventBus, NotificationService notificationService, ConnectionService connectionService) {
        this.eventBus = eventBus;
        this.notificationService = notificationService;
        eventBus.register(this);
        this.connectionService = connectionService;
    }

    @Subscribe
    public void handleConnectionGrantEvent(ConnectionRequestEvent event) throws Exception {
        Id<User, Long> requestedUserId = event.getRequestedUserId();
        Id<User, Long> targetId = event.getTargetId();
        log.info("{} granted a requested connection with {} !", targetId, requestedUserId);

        try {
            log.info("Try to send push for {}", event);
            notificationService.notifyUser(requestedUserId,
                    new PushMessage(
                            "connection granted!",
                            "user/" + targetId.value() + "/post/list",
                            "You can check new friend's posts"
                    ));
        } catch (Exception e) {
            log.error("Got error while handling event ConnectionGrantEvent " + event.toString(), e);
            throw new NotNotifiedException(ConnectionGrantEvent.class, e.getMessage(), event);
        }
    }

    @Override
    public void close() throws Exception {
        eventBus.unregister(this);
    }
}
