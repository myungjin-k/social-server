package me.myungjin.social.service.user;

import com.google.common.eventbus.EventBus;
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
    public Optional<Connection> addConnection(Id<User, Long> userId, Id<User, Long> targetId, From from) {
        checkNotNull(userId, "userId must be provided.");
        checkNotNull(targetId, "targetId must be provided.");

        return findUser(targetId).map( user -> {
            if(!connectionRepository.existsById(userId, targetId)){
                Connection newConnection = saveConnection(new Connection(userId, targetId, from));
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
            return connection;
        }).orElseThrow(() -> new NotFoundException(Connection.class, userId, targetId));
    }

    @Transactional(readOnly = true)
    public List<Connection> findUngrantedConnections(Id<User, Long> targetId) {
        checkNotNull(targetId, "targetId must be provided.");

        return connectionRepository.findUngrantedConnectionsById(targetId);
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

    private Connection saveConnection(Connection connection) {
        return connectionRepository.save(connection);
    }

}
