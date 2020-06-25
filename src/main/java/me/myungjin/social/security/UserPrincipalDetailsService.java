package me.myungjin.social.security;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserPrincipalDetailsService(PasswordEncoder passwordEncoder, UserRepository userRepository){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public User login(User user, String credential) {
        user.login(passwordEncoder, credential);
        user.afterLoginSuccess();
        update(user);
        return user;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        checkNotNull(principal, "email must be provided.");
        return findByEmail(principal)
                .map(UserPrincipal::of)
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
