package me.myungjin.social.configure;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import me.myungjin.social.controller.event.EventExceptionHandler;
import me.myungjin.social.controller.event.listener.CommentCreateEventListener;
import me.myungjin.social.controller.event.listener.ConnectionGrantEventListener;
import me.myungjin.social.controller.event.listener.ConnectionRequestEventListener;
import me.myungjin.social.controller.event.listener.JoinEventListener;
import me.myungjin.social.service.notification.NotificationService;
import me.myungjin.social.service.post.PostService;
import me.myungjin.social.service.user.ConnectionService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConfigurationProperties(prefix = "eventbus")
public class EventConfigure {

  private int asyncPoolCore;
  private int asyncPoolMax;
  private int asyncPoolQueue;

  public int getAsyncPoolCore() {
    return asyncPoolCore;
  }

  public void setAsyncPoolCore(int asyncPoolCore) {
    this.asyncPoolCore = asyncPoolCore;
  }

  public int getAsyncPoolMax() {
    return asyncPoolMax;
  }

  public void setAsyncPoolMax(int asyncPoolMax) {
    this.asyncPoolMax = asyncPoolMax;
  }

  public int getAsyncPoolQueue() {
    return asyncPoolQueue;
  }

  public void setAsyncPoolQueue(int asyncPoolQueue) {
    this.asyncPoolQueue = asyncPoolQueue;
  }

  @Bean
  public TaskExecutor eventTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("EventBus-");
    executor.setCorePoolSize(asyncPoolCore);
    executor.setMaxPoolSize(asyncPoolMax);
    executor.setQueueCapacity(asyncPoolQueue);
    executor.afterPropertiesSet();
    return executor;
  }

  @Bean
  public EventExceptionHandler eventExceptionHandler() {
    return new EventExceptionHandler();
  }

  @Bean
  public EventBus eventBus(TaskExecutor eventTaskExecutor, EventExceptionHandler eventExceptionHandler) {
    return new AsyncEventBus(eventTaskExecutor, eventExceptionHandler);
  }

  @Bean(destroyMethod = "close")
  public JoinEventListener joinEventListener(EventBus eventBus, NotificationService notificationService) {
    return new JoinEventListener(eventBus, notificationService);
  }

  @Bean(destroyMethod = "close")
  public CommentCreateEventListener commentCreateEventListener(
          EventBus eventBus, NotificationService notificationService, PostService postService) {
    return new CommentCreateEventListener(eventBus, notificationService, postService);
  }

  @Bean(destroyMethod = "close")
  public ConnectionRequestEventListener connectionRequestEventListener(
          EventBus eventBus, NotificationService notificationService, ConnectionService connectionService) {
    return new ConnectionRequestEventListener(eventBus, notificationService, connectionService);
  }

  @Bean(destroyMethod = "close")
  public ConnectionGrantEventListener connectionGrantEventListener(
          EventBus eventBus, NotificationService notificationService, ConnectionService connectionService) {
    return new ConnectionGrantEventListener(eventBus, notificationService, connectionService);
  }
}
