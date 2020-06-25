package me.myungjin.social.security;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserPrincipalDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public User login(User user) {
        // TODO password를 왜 확인하는지?
        // user.login(passwordEncoder, password);
        user.afterLoginSuccess();
        update(user);
        return user;

    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        checkNotNull(principal, "email must be provided.");
        return (UserDetails) userRepository.findByEmail(principal)
                .map(this::login)
                .map((Function<User, Object>) UserPrincipal::new)
                .orElseThrow(() -> new NotFoundException(User.class, principal));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        checkNotNull(email, "email must be provided.");
        return userRepository.findByEmail(email);
    }

    private void update(User user) {
        userRepository.update(user);
    }
}
