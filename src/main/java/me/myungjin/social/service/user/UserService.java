package me.myungjin.social.service.user;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import me.myungjin.social.aws.S3Client;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.AttachedFile;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.ConnectedUser;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

@Service
public class UserService {

    private Logger log = LoggerFactory.getLogger(UserService.class);

    private final S3Client s3Client;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;


    public UserService(S3Client s3Client, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.s3Client = s3Client;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    private Optional<String> uploadProfileImage(AttachedFile profileFile) {
        String profileImageUrl = null;
        if (profileFile != null) {
            String key = profileFile.randomName("profiles", "jpeg");
            try {
                profileImageUrl = s3Client.upload(profileFile.inputStream(), profileFile.length(), key, profileFile.getContentType(), null);
            } catch (AmazonS3Exception e) {
                log.warn("Amazon S3 error (key: {}): {}", key, e.getMessage(), e);
            }
        }
        return ofNullable(profileImageUrl);
    }

    @Transactional
    public User join(String name, String email, String password, AttachedFile profileFile) {
        checkNotNull(name, "name must be provided.");
        checkNotNull(email, "email must be provided.");
        checkNotNull(password, "password must be provided.");

        User user = new User(
                name,
                email,
                passwordEncoder.encode(password),
                uploadProfileImage(profileFile).orElse(null)
        );
        return findByEmail(email).map(u -> new User.Builder(user).seq(-2L).build()).orElseGet(() -> save(user));
    }

    @Transactional
    public User login(String email, String password) {
        checkNotNull(email, "email must be provided.");
        checkNotNull(password, "password must be provided.");

        User user = findByEmail(email)
                .orElseThrow(() -> new NotFoundException(User.class, email));
       // TODO password를 왜 확인하는지? -> 인증 과정에서 따로 credential 검증을 안 함.
        user.login(passwordEncoder, password);
        user.afterLoginSuccess();
        update(user);
        return user;
    }

    @Transactional
    public Optional<User> modify(Id<User, Long> userId, String name, AttachedFile profileFile) {
        checkNotNull(userId.value(), "userId must be provided.");
        return findById(userId)
                .map(me -> {
                    if(name != null)
                        me.modifyName(name);
                    if(profileFile != null)
                        me.modifyProfileImageUrl(uploadProfileImage(profileFile).orElse(null));
                    update(me);
                    return me;
                });
    }

    @Transactional
    public Optional<User> modifyPassword(Id<User, Long> userId, String oldPassword, String newPassword) {
        checkNotNull(userId.value(), "userId must be provided.");
        checkNotNull(oldPassword, "oldPassword must be provided.");
        checkNotNull(newPassword, "newPassword must be provided.");
        return findById(userId)
                .map(me -> {
                    me.login(passwordEncoder, oldPassword);
                    me.modifyPassword(passwordEncoder.encode(newPassword));
                    update(me);
                    return me;
                });
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

    @Transactional(readOnly = true)
    public List<ConnectedUser> findAllConnectedUser(Id<User, Long> userId) {
        checkNotNull(userId, "userId must be provided.");

        return userRepository.findAllConnectedUser(userId);
    }

    @Transactional(readOnly = true)
    public List<Id<User, Long>> findConnectedIds(Id<User, Long> userId) {
        checkNotNull(userId, "userId must be provided.");

        return userRepository.findConnectedIds(userId);
    }

    private User save(User user) {
        return userRepository.save(user);
    }

    private void update(User user) {
        userRepository.update(user);
    }



}
