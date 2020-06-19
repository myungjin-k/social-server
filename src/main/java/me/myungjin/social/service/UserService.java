package me.myungjin.social.service;

import me.myungjin.social.error.DuplicateKeyException;
import me.myungjin.social.model.User;
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
        if(findByEmail(email).isPresent()){
            throw new DuplicateKeyException(User.class, email);
        }
        return save(new User(name, email, password));
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
}
