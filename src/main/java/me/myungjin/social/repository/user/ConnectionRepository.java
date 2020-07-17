package me.myungjin.social.repository.user;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.ConnectedUser;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository {

    boolean existsById(Id<User, Long> userId, Id<User, Long> targetId);

    Connection save(Connection connection);

}
