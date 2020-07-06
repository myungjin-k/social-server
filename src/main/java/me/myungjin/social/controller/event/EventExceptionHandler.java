package me.myungjin.social.controller.event;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import me.myungjin.social.error.NotNotifiedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventExceptionHandler implements SubscriberExceptionHandler {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void handleException(Throwable exception, SubscriberExceptionContext context) {
    if(exception instanceof NotNotifiedException)
      log.warn("Notification exception occured [{} > {}]",
              context.getSubscriber(), context.getSubscriberMethod(), exception);
   else log.error("Unexpected event exception occurred [{} > {} with {}]: {}",
        context.getSubscriber(), context.getSubscriberMethod(), context.getEvent(), exception.getMessage(), exception);
  }
}