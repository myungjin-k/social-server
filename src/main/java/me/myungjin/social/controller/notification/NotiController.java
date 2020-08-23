package me.myungjin.social.controller.notification;

import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.security.JwtAuthentication;
import me.myungjin.social.service.notification.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.myungjin.social.controller.ApiResult.OK;

@RestController
@RequestMapping("api")
public class NotiController {

    private final NotificationService notificationService;

    public NotiController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(path = "noti/list")
    public ApiResult<List<Noti>> findAll(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(notificationService.findAll(authentication.id));
    }

    @DeleteMapping(path = "noti/{notiId}")
    public ApiResult<Noti> remove(@AuthenticationPrincipal JwtAuthentication authentication,
                                         @PathVariable Long notiId) {
        return OK(notificationService.remove(Id.of(Noti.class, notiId), authentication.id));
    }
}
