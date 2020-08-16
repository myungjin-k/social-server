package me.myungjin.social.repository.user;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository {

    Optional<Connection> findById(Id<User, Long> userId, Id<User, Long> targetId);

    boolean existsById(Id<User, Long> userId, Id<User, Long> targetId);

    Connection save(Connection connection);

    List<Connection> findUngrantedConnectionsByUserId(Id<User, Long> userId);

    List<Connection> findConnectionsByTargetId(Id<User, Long> targetId);

    List<Connection> findUngrantedConnectionsByTargetId(Id<User, Long> targetId);

    void grant(Connection connection);

    Connection delete(Connection connection);
}
