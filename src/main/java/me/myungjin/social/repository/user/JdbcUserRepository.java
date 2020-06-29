package me.myungjin.social.repository.user;

import me.myungjin.social.model.user.Role;
import me.myungjin.social.model.user.User;
import me.myungjin.social.model.commons.Id;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static me.myungjin.social.util.DateTimeUtils.dateTimeOf;
import static me.myungjin.social.util.DateTimeUtils.timestampOf;

@Repository
public class JdbcUserRepository implements UserRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAllUsers() {
        return jdbcTemplate.query("select * from users order by seq desc", mapper);
    }

    @Override
    public Optional<User> findById(Id<User, Long> userId) {
        List<User> results = jdbcTemplate.query("SELECT * FROM USERS WHERE SEQ = ?", new Object[]{userId.value()}, mapper);
        return Optional.ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public boolean existsByEmail(String email) {
        Optional<Integer> result = Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT COUNT(*) AS CNT FROM USERS T WHERE T.EMAIL = ?"
                        , new Object[]{email}
                        , (resultSet, rowNum) -> resultSet.getInt("cnt")
                )
        );
        return  result.orElse(0) > 0;
    }

    public Optional<User> findByEmail(String email) {
        List<User> results = jdbcTemplate.query("SELECT * FROM USERS WHERE EMAIL = ?", new Object[]{email}, mapper);
        return Optional.ofNullable(results.isEmpty() ? null : results.get(0));
    }


    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users(seq,email,name,passwd,login_count,last_login_at,create_at) VALUES (null,?,?,?,?,?,?)", new String[]{"seq"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getLoginCount());
            ps.setTimestamp(5, timestampOf(user.getLastLoginAt().orElse(null)));
            ps.setTimestamp(6, timestampOf(user.getCreateAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long generatedSeq = key != null ? key.longValue() : -1;
        return new User.Builder(user)
                .seq(generatedSeq)
                .build();
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("UPDATE users SET passwd=?, name=?, login_count=?,last_login_at=? WHERE seq=?",
            user.getPassword(),
            user.getName(),
            user.getLoginCount(),
            user.getLastLoginAt().orElse(null),
            user.getSeq()
        );
    }

    @Override
    public List<Id<User, Long>> findConnectedIds(Id<User, Long> userId) {
        return jdbcTemplate.query(
                "SELECT target_seq FROM connections WHERE user_seq=? AND granted_at is not null ORDER BY target_seq",
                new Object[]{userId.value()},
                (rs, rowNum) -> Id.of(User.class, rs.getLong("target_seq"))
        );
    }

    static RowMapper<User> mapper = (rs, rowNum) -> new User.Builder()
            .seq(rs.getLong("seq"))
            .email((rs.getString("email")))
            .name(rs.getString("name"))
            .password(rs.getString("passwd"))
            .loginCount(rs.getInt("login_count"))
            .lastLoginAt(dateTimeOf(rs.getTimestamp("last_login_at")))
            .createAt(dateTimeOf(rs.getTimestamp("create_at")))
            .role(Role.of(rs.getString("role")))
            .build();
}
