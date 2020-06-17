package me.myungjin.social.controller;

import me.myungjin.social.model.User;
import me.myungjin.social.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "users")
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }
}
