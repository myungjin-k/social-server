package me.myungjin.social.controller.notification;

import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.security.JwtAuthentication;
import me.myungjin.social.service.notification.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static me.myungjin.social.controller.ApiResult.OK;


@RestController
@RequestMapping("api")
public class SubscribeController {

  private final NotificationService notificationService;

  public SubscribeController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/subscribe")
  public ApiResult<Subscription> subscribe(@RequestBody SubscribeRequest request, @AuthenticationPrincipal JwtAuthentication authentication) {
    Subscription subscribe = notificationService.subscribe(request.newSubscription(authentication.id));
    return OK(subscribe);
  }

}
