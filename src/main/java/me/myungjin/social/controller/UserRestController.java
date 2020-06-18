package me.myungjin.social.controller;

import me.myungjin.social.model.User;
import me.myungjin.social.model.api.request.JoinRequest;
import me.myungjin.social.model.api.response.ApiResult;
import me.myungjin.social.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.myungjin.social.model.api.response.ApiResult.OK;

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
        return OK(
                userService.join(joinRequest.getName(), joinRequest.getPrincipal(), joinRequest.getCredentials())
        );
    }

    @GetMapping(path = "user/me")
    public ApiResult<User> me(Long userId) {
        return OK(
                userService.findById(userId).orElse(new User.Builder().build())
        );
    }
}
