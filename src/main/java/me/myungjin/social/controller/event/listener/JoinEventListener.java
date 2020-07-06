package me.myungjin.social.controller.event.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.myungjin.social.controller.event.JoinEvent;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinEventListener implements AutoCloseable {

  private Logger log = LoggerFactory.getLogger(JoinEventListener.class);

  private EventBus eventBus;

  private final NotificationService notificationService;


  public JoinEventListener(EventBus eventBus, NotificationService notificationService) {
    this.eventBus = eventBus;
    this.notificationService = notificationService;
    eventBus.register(this);
  }

  @Subscribe
  public void handleJoinEvent(JoinEvent event) {
    String name = event.getName();
    Id<User, Long> userId = event.getUserId();
    log.info("user {}, userId {} joined!", name, userId);

    try {
      log.info("Try to send push for {}", event);
      notificationService.notifyAll(new PushMessage(
          name + " Joined!",
          "/friends/" + userId.value(),
          "Please send welcome message"
      ));
    } catch (Exception e) {
      log.error("Got error while handling event JoinEvent " + event.toString(), e);
      e.printStackTrace();
    }
  }

  @Override
  public void close() throws Exception {
    eventBus.unregister(this);
  }

}
