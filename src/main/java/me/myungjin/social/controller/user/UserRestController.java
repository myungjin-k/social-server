package me.myungjin.social.controller.user;

import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.error.DuplicateKeyException;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.ConnectedUser;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.From;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.JwtAuthentication;
import me.myungjin.social.service.user.ConnectionService;
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

    private final ConnectionService connectionService;

    public UserRestController(UserService userService, ConnectionService connectionService) {
        this.userService = userService;
        this.connectionService = connectionService;
    }

    @GetMapping(path = "users")
    public ApiResult<List<User>> findAllUsers() {
        return OK(userService.findAllUsers());
    }

    @PostMapping(path = "user/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<User> join(@ModelAttribute JoinRequest joinRequest,
                                @RequestPart(required = false) MultipartFile file) throws IOException {
        User user = userService.join(
                joinRequest.getName(),
                joinRequest.getPrincipal(),
                joinRequest.getCredentials(),
                toAttachedFile(file));
        if(user.getSeq() == -2)
            throw new DuplicateKeyException(User.class, user.getEmail());
        return OK(user);
    }

    @GetMapping(path = "user/me")
    public ApiResult<User> me(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(
                userService.findById(authentication.id).orElseThrow(() -> new NotFoundException(User.class, authentication.id))
        );
    }

    @PutMapping(path = "user/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<User> modifyInfo(@AuthenticationPrincipal JwtAuthentication authentication,
                                  @ModelAttribute ModifyUserRequest userModifyRequest,
                                  @RequestPart(required = false) MultipartFile file) throws IOException {
        return OK(
                userService.modify(authentication.id, userModifyRequest.getName(), toAttachedFile(file))
                        .orElseThrow(() -> new NotFoundException(User.class, authentication.id))
        );
    }

    @PatchMapping(path = "user/me/password")
    public ApiResult<User> modifyPassword(@AuthenticationPrincipal JwtAuthentication authentication,
                                      @RequestBody ModifyPasswordRequest passwordRequest){
        return OK(
                userService.modifyPassword(authentication.id, passwordRequest.getOldPassword(), passwordRequest.getPassword())
                        .orElseThrow(() -> new NotFoundException(User.class, authentication.id))
        );
    }

    @GetMapping(path = "user/connections")
    public ApiResult<List<ConnectedUser>> connections(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(
                userService.findAllConnectedUser(authentication.id)
        );
    }

    @PostMapping(path = "user/connections/{targetId}")
    public ApiResult<Connection> requestConnection(@AuthenticationPrincipal JwtAuthentication authentication,
                                               @PathVariable Long targetId) {
        return OK(
                connectionService.add(authentication.id, Id.of(User.class, targetId), new From(authentication.email, authentication.name))
                        .orElse(null)
        );
    }

    @GetMapping(path = "user/connections/proceeding")
    public ApiResult<List<Connection>> proceedingConnectionRequests(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(
               connectionService.findProceedingConnectionRequests(authentication.id)
        );
    }
 // 구독 취소와 같은 개념?
    @DeleteMapping(path = "user/connections/{targetId}")
    public ApiResult<Connection> quitConnection(@AuthenticationPrincipal JwtAuthentication authentication,
                                                @PathVariable Long targetId){
        return OK(connectionService.quit(authentication.id, Id.of(User.class, targetId)));
    }

    @GetMapping(path = "user/connections/followers")
    public ApiResult<List<Connection>> followers(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(connectionService.findFollowers(authentication.id));
    }

    @GetMapping(path = "user/connections/grant")
    public ApiResult<List<Connection>> ungrantedConnections(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(connectionService.findConnectionsNeedToBeGrantedByMe(authentication.id));
    }

    @PatchMapping(path = "user/connections/grant/{userId}")
    public ApiResult<Connection> grant(@AuthenticationPrincipal JwtAuthentication authentication,
                                       @PathVariable Long userId){
        return OK(connectionService.grant(Id.of(User.class, userId), authentication.id));
    }

}
