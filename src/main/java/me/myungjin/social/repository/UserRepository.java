package me.myungjin.social.repository;

import me.myungjin.social.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAllUsers();
}
