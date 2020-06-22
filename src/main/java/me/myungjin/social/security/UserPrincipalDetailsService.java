package me.myungjin.social.security;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserPrincipalDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByEmail(principal)
                .map((Function<User, Object>) UserPrincipal::new)
                .orElseThrow(() -> new NotFoundException(User.class, principal));
    }
}
