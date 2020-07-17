package me.myungjin.social.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.myungjin.social.model.commons.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class Connection {

    private final Long seq;

    @JsonIgnore
    private final Id<User, Long> userId;

    @JsonIgnore
    private final Id<User, Long> targetId;

    private LocalDateTime grantedAt;

    private final LocalDateTime createAt;

    public Connection(Id<User, Long> userId, Id<User, Long> targetId){
        this(null, userId, targetId, null, null);
    }

    public Connection(Long seq, Id<User, Long> userId, Id<User, Long> targetId, LocalDateTime grantedAt, LocalDateTime createAt) {
        checkNotNull(userId, "userId must be provided.");
        checkNotNull(targetId, "targetId must be provided.");

        this.seq = seq;
        this.userId = userId;
        this.targetId = targetId;
        this.grantedAt = grantedAt;
        this.createAt = defaultIfNull(createAt, now());
    }

    public Long getSeq() {
        return seq;
    }

    public Id<User, Long> getUserId() {
        return userId;
    }

    public Id<User, Long> getTargetId() {
        return targetId;
    }

    public Optional<LocalDateTime> getGrantedAt() {
        return ofNullable(grantedAt);
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("seq", seq)
                .append("userId", userId)
                .append("targetId", targetId)
                .append("grantedAt", grantedAt)
                .append("createAt", createAt)
                .toString();
    }

    public static class Builder {

        private Long seq;
        private Id<User, Long> userId;
        private Id<User, Long> targetId;
        private LocalDateTime grantedAt;
        private LocalDateTime createAt;


        public Builder() {
        }

        public Builder(Connection connection) {
            this.seq = connection.seq;
            this.userId = connection.userId;
            this.targetId = connection.targetId;
            this.grantedAt = connection.grantedAt;
            this.createAt = connection.createAt;
        }

        public Builder seq(Long seq){
            this.seq = seq;
            return this;
        }

        public Builder userId(Id<User, Long> userId){
            this.userId = userId;
            return this;
        }

        public Builder targetId(Id<User, Long> targetId){
            this.targetId = targetId;
            return this;
        }

        public Builder grantedAt(LocalDateTime grantedAt){
            this.grantedAt = grantedAt;
            return this;
        }

        public Builder createAt(LocalDateTime createAt){
            this.createAt = createAt;
            return this;
        }

        public Connection build() {
            return new Connection(seq, userId, targetId, grantedAt, createAt);
        }
    }
}
