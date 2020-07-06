package me.myungjin.social.repository.subscription;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Subscription;
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
public class JdbcSubscriptionRepository implements SubscriptionRepository{

    private final JdbcTemplate jdbcTemplate;

    public JdbcSubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Subscription save(Subscription subscription) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO subscriptions(seq,user_seq,endpoint,public_key,auth,create_at) VALUES (null,?,?,?,?,?)", new String[]{"seq"});
            ps.setLong(1, subscription.getUserId().value());
            ps.setString(2, subscription.getNotificationEndPoint());
            ps.setString(3, subscription.getPublicKey());
            ps.setString(4, subscription.getAuth());
            ps.setTimestamp(5, timestampOf(subscription.getCreateAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long generatedSeq = key != null ? key.longValue() : -1;
        return new Subscription.Builder(subscription)
                .seq(generatedSeq)
                .build();
    }

    @Override
    public Optional<Subscription> findByUserId(Id<User, Long> userId) {
        List<Subscription> results = jdbcTemplate.query(
                "SELECT " +
                        "s.* " +
                        "FROM " +
                        "subscriptions s " +
                        "WHERE " +
                        "s.user_seq=? ",
                new Object[]{userId.value()},
                mapper
        );
        return ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public List<Subscription> findAll() {
        return jdbcTemplate.query(
                "SELECT " +
                        "s.* " +
                        "FROM " +
                        "subscriptions s " +
                        "ORDER BY " +
                        "s.SEQ DESC ",
                mapper
        );
    }


    @Override
    public void update(Subscription subscription) {
        jdbcTemplate.update(
                "UPDATE subscriptions SET endpoint=?,public_key=?,auth=? WHERE seq=?",
                subscription.getNotificationEndPoint(),
                subscription.getPublicKey(),
                subscription.getAuth(),
                subscription.getSeq()
        );
    }

    static RowMapper<Subscription> mapper = (rs, rowNum) -> new Subscription.Builder()
            .seq(rs.getLong("seq"))
            .userId(Id.of(User.class, rs.getLong("user_seq")))
            .notificationEndPoint(rs.getString("endpoint"))
            .publicKey(rs.getString("public_key"))
            .auth(rs.getString("auth"))
            .createAt(dateTimeOf(rs.getTimestamp("create_at")))
            .build();
}
