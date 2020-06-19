package me.myungjin.social.repository;

import me.myungjin.social.model.User;
import me.myungjin.social.model.commons.Id;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllUsers();

    Optional<User> findById(Id<User, Long> userId);

    Optional<User> findByEmail(String Email);

    boolean existsByEmail(String Email);

    User save(User user);

    void update(User user);
}
