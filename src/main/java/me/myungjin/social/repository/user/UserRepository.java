package me.myungjin.social.repository.user;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.ConnectedUser;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAllUsers();

    Optional<User> findById(Id<User, Long> userId);

    Optional<User> findByEmail(String Email);

    boolean existsByEmail(String Email);

    User save(User user);

    void update(User user);

    List<ConnectedUser> findAllConnectedUser(Id<User, Long> userId);

    List<Id<User, Long>> findConnectedIds(Id<User, Long> userId);

}
