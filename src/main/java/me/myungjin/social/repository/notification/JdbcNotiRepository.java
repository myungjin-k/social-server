package me.myungjin.social.repository.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static me.myungjin.social.util.DateTimeUtils.dateTimeOf;
import static me.myungjin.social.util.DateTimeUtils.timestampOf;

@Repository
public class JdbcNotiRepository implements NotiRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcNotiRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Noti save(Noti noti) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps =
                    conn.prepareStatement("INSERT INTO notifications(seq, user_seq, message, click_target, create_at) VALUES (null,?,?,?,?)", new String[]{"seq"});
            ps.setLong(1, noti.getUserId().value());
            ps.setString(2, noti.getMessage());
            ps.setString(3, noti.getClickTarget());
            ps.setTimestamp(4, timestampOf(noti.getCreateAt()));
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        long generatedSeq = key != null ? key.longValue() : -1;
        return new Noti.Builder(noti)
                .seq(generatedSeq)
                .build();
    }

    @Override
    public void delete(Id<Noti, Long> notificationId) {

    }

    @Override
    public Optional<Noti> findById(Id<Noti, Long> notificationId, Id<User, Long> userId) {
        List<Noti> results = jdbcTemplate.query(
                "SELECT n.*,u.email,u.name FROM notifications n JOIN users u ON n.user_seq=u.seq AND n.user_seq = ? WHERE n.seq=?",
                new Object[]{userId.value(), notificationId.value()},
                mapper
        );
        return ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public List<Noti> findAll(Id<User, Long> userId) {
        return jdbcTemplate.query(
                "SELECT n.*,u.email,u.name FROM notifications n JOIN users u ON n.user_seq=u.seq AND n.user_seq = ?",
                new Object[]{userId.value()},
                mapper
        );
    }

    static RowMapper<Noti> mapper = (rs, rowNum) -> new Noti.Builder()
            .seq(rs.getLong("seq"))
            .userId(Id.of(User.class, rs.getLong("user_seq")))
            .message(rs.getString("message"))
            .clickTarget(rs.getString("click_target"))
            .createAt(dateTimeOf(rs.getTimestamp("create_at")))
            .build();

}
