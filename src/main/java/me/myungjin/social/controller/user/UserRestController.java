package me.myungjin.social.controller.user;

import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.error.DuplicateKeyException;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.ConnectedUser;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.JwtAuthentication;
import me.myungjin.social.service.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static me.myungjin.social.controller.ApiResult.OK;
import static me.myungjin.social.model.commons.AttachedFile.toAttachedFile;

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



    @GetMapping(path = "user/me")
    public ApiResult<User> me(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(
                userService.findById(authentication.id).orElseThrow(() -> new NotFoundException(User.class, authentication.id))
        );
    }

    @GetMapping(path = "user/connections")
    public ApiResult<List<ConnectedUser>> connections(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(
                userService.findAllConnectedUser(authentication.id)
        );
    }
}
