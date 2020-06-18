package me.myungjin.social.repository;

import me.myungjin.social.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllUsers();

    Optional<User> findById(Long seq);

    Optional<User> findByEmail(String Email);

    boolean existsByEmail(String Email);

    User save(User user);

    void update(User user);
}
