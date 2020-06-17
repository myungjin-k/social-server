package me.myungjin.social.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class User {

    private final Long seq;

    private final String email;

    private final String name;

    private String password;

    private int loginCount;

    private LocalDateTime lastLoginAt;

    private final LocalDateTime createAt;

    public User(Long seq, String email, String name, String password, int loginCount, LocalDateTime lastLoginAt, LocalDateTime createAt) {
        checkNotNull(email, "email must be provided.");
        checkNotNull(password, "password must be provided.");
        this.seq = seq;
        this.email = email;
        this.name = name;
        this.password = password;
        this.loginCount = loginCount;
        this.lastLoginAt = lastLoginAt;
        this.createAt = defaultIfNull(createAt, now());
    }

    public Long getSeq() {
        return seq;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public Optional<LocalDateTime> getLastLoginAt() {
        return ofNullable(lastLoginAt);
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
    // equals 와 hashcode
    // https://jojoldu.tistory.com/134
    @Override
    public int hashCode() {
        return Objects.hash(seq);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        if(!Objects.equals(seq, that.seq)) return false;
        if(!Objects.equals(email, that.email)) return false;
        if(!Objects.equals(name, that.name)) return false;
        return Objects.equals(createAt, that.createAt);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("seq", seq)
                .append("email", email)
                .append("name", name)
                .append("password", "[PROTECTED]")
                .append("loginCount", loginCount)
                .append("lastLoginAt", lastLoginAt)
                .append("createAt", createAt)
                .toString();
    }

    public static class Builder {
        private Long seq;
        private String email;
        private String name;
        private String password;
        private int loginCount;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createAt;

        public Builder() {
        }

        public Builder(User user) {
            this.seq = user.seq;
            this.email = user.email;
            this.name = user.name;
            this.password = user.password;
            this.loginCount = user.loginCount;
            this.lastLoginAt = user.lastLoginAt;
            this.createAt = user.createAt;
        }

        public Builder seq(Long seq){
            this.seq = seq;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder password(String password){
            this.password = password;
            return this;
        }

        public Builder loginCount(int loginCount){
            this.loginCount = loginCount;
            return this;
        }

        public Builder lastLoginAt(LocalDateTime lastLoginAt){
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public Builder createAt(LocalDateTime createAt){
            this.createAt = createAt;
            return this;
        }

        public User build() {
            return new User(seq, email, name, password, loginCount, lastLoginAt, createAt);
        }
    }
}
