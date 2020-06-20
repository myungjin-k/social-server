package me.myungjin.social.service;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.User;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Transactional
    public User join(String name, String email, String password) {
        checkNotNull(name, "name must be provided.");
        checkNotNull(email, "email must be provided.");
        checkNotNull(password, "password must be provided.");
        User user = new User(name, email, password);
        return findByEmail(email).map(u -> new User.Builder(user).seq(-2L).build()).orElseGet(() -> save(user));
    }

    @Transactional
    public User login(String email, String password) {
        checkNotNull(email, "email must be provided.");
        checkNotNull(password, "password must be provided.");

        User user = findByEmail(email)
                .orElseThrow(() -> new NotFoundException(User.class, email));
       // TODO password를 왜 확인하는지?
       // user.login(passwordEncoder, password);
        user.afterLoginSuccess();
        update(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Id<User, Long> userId) {
        checkNotNull(userId.value(), "userId must be provided.");
        return userRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        checkNotNull(email, "email must be provided.");
        return userRepository.findByEmail(email);
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    private void update(User user) {
        userRepository.update(user);
    }

    public List<Id<User, Long>> findConnectedIds(Id<User, Long> userId) {
        checkNotNull(userId, "userId must be provided.");
        return userRepository.findConnectedIds(userId);
    }
}
