package me.myungjin.social.controller.user;


import me.myungjin.social.controller.ApiError;
import me.myungjin.social.model.user.User;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class JoinResponse {
    private boolean success;
    private User response;
    private ApiError error;

    public JoinResponse(boolean success, User response, ApiError error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }

    public User getResponse() {
        return response;
    }

    public Optional<ApiError> getError() {
        return ofNullable(error);
    }

    public static JoinResponse success(User user){
        return new JoinResponse(true, user, null);
    }
    public static JoinResponse fail(User user, ApiError error){
        return new JoinResponse(false, user, error);
    }
}
