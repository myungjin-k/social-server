package me.myungjin.social.service.user;

import com.google.common.eventbus.EventBus;
import me.myungjin.social.controller.event.ConnectionGrantEvent;
import me.myungjin.social.controller.event.ConnectionRequestEvent;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.From;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.user.ConnectionRepository;
import me.myungjin.social.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service

public class ConnectionService {

    private final ConnectionRepository connectionRepository;

    private final UserRepository userRepository;

    private final EventBus eventBus;

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository, EventBus eventBus) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Transactional
    public Optional<Connection> add(Id<User, Long> userId, Id<User, Long> targetId, From from) {
        checkNotNull(userId, "userId must be provided.");
        checkNotNull(targetId, "targetId must be provided.");

        return findUser(targetId).map( user -> {
            if(!connectionRepository.existsById(userId, targetId)){
                Connection newConnection = save(new Connection(userId, targetId, from));
                eventBus.post(new ConnectionRequestEvent(userId, targetId));
                return newConnection;
            }
            return null;
        });
    }

    @Transactional
    public Connection grant(Id<User, Long> userId, Id<User, Long> targetId){
        return findById(userId, targetId).map(connection -> {
            connection.grant();
            connectionRepository.grant(connection);
            eventBus.post(new ConnectionGrantEvent(userId, targetId));
            return connection;
        }).orElseThrow(() -> new NotFoundException(Connection.class, userId, targetId));
    }

    @Transactional
    public Connection quit(Id<User, Long> userId, Id<User, Long> targetId){
        return findById(userId, targetId)
                .map(this::delete)
                .orElseThrow(() -> new NotFoundException(Connection.class, userId, targetId));
    }

    @Transactional(readOnly = true)
    public List<Connection> findFollowers(Id<User, Long> targetId) {
        checkNotNull(targetId, "targetId must be provided.");

        return connectionRepository.findConnectionsByTargetId(targetId);
    }

    @Transactional(readOnly = true)
    public List<Connection> findConnectionsNeedToBeGrantedByMe(Id<User, Long> targetId) {
        checkNotNull(targetId, "targetId must be provided.");

        return connectionRepository.findUngrantedConnectionsByTargetId(targetId);
    }

    @Transactional(readOnly = true)
    public List<Connection> findProceedingConnectionRequests(Id<User, Long> userId) {
        checkNotNull(userId, "userId must be provided.");

        return connectionRepository.findUngrantedConnectionsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Connection> findById(Id<User, Long> userId, Id<User, Long> targetId) {
        checkNotNull(userId, "userId must be provided.");
        checkNotNull(targetId, "targetId must be provided.");

        return connectionRepository.findById(userId, targetId);
    }


    private Optional<User> findUser(Id<User, Long> targetId) {
        checkNotNull(targetId, "targetId must be provided.");

        return userRepository.findById(targetId);
    }

    private Connection save(Connection connection) {
        return connectionRepository.save(connection);
    }

    private Connection delete(Connection connection){
        return connectionRepository.delete(connection);
    }

}
