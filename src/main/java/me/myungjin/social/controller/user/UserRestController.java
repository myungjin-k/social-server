package me.myungjin.social.controller.user;

import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.error.DuplicateKeyException;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.api.request.JoinRequest;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.UserPrincipal;
import me.myungjin.social.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.myungjin.social.controller.ApiResult.OK;

@RestController
@RequestMapping("api")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "users")
    public ApiResult<List<User>> findAllUsers() {
        return OK(userService.findAllUsers());
    }

    @PostMapping(path = "user/join")
    public ApiResult<User> join(@RequestBody JoinRequest joinRequest) {
        User user = userService.join(joinRequest.getName(), joinRequest.getPrincipal(), joinRequest.getCredentials());
        if(user.getSeq() == -2)
            throw new DuplicateKeyException(User.class, user.getEmail());
        return OK(user);
    }

    @GetMapping(path = "user/me")
    public ApiResult<User> me(@AuthenticationPrincipal UserPrincipal authentication) {
        return OK(
                userService.findById(authentication.id)
                        .orElseThrow(() -> new NotFoundException(User.class, authentication.id))
        );
    }
}
