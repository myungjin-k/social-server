package me.myungjin.social.repository.user;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.From;
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
public class JdbcConnectionRepository implements ConnectionRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcConnectionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Connection> findById(Id<User, Long> userId, Id<User, Long> targetId) {
        List<Connection> results = jdbcTemplate.query("select c.*, u.NAME, u.EMAIL from connections c JOIN users u ON c.user_seq=u.seq where c.user_seq = ? and c.target_seq = ? order by c.seq desc",
                new Object[]{userId.value(), targetId.value()}, mapper);
        return ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public boolean existsById(Id<User, Long> userId, Id<User, Long> targetId) {
        Optional<Integer> result = ofNullable(
                jdbcTemplate.queryForObject("SELECT COUNT(*) AS CNT FROM CONNECTIONS T WHERE T.USER_SEQ = ? AND T.TARGET_SEQ = ?"
                        , new Object[]{userId.value(), targetId.value()}
                        , (resultSet, rowNum) -> resultSet.getInt("cnt")
                )
        );
        return  result.orElse(0) > 0;
    }

    @Override
    public Connection save(Connection connection) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO connections(seq,user_seq,target_seq,granted_at,create_at) VALUES (null,?,?,?,?)", new String[]{"seq"});
            ps.setLong(1, connection.getUserId().value());
            ps.setLong(2, connection.getTargetId().value());
            ps.setTimestamp(3, timestampOf(connection.getGrantedAt().orElse(null)));
            ps.setTimestamp(4, timestampOf(connection.getCreateAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long generatedSeq = key != null ? key.longValue() : -1;
        return new Connection.Builder(connection)
                .seq(generatedSeq)
                .build();
    }

    @Override
    public List<Connection> findUngrantedConnectionsByUserId(Id<User, Long> userId) {
        return jdbcTemplate.query("select c.*, u.NAME, u.EMAIL from connections c JOIN users u ON c.user_seq=u.seq where user_seq = ? and c.granted_at is null order by seq desc",
                new Object[]{userId.value()}, mapper);
    }

    @Override
    public List<Connection> findConnectionsByTargetId(Id<User, Long> targetId) {
        return jdbcTemplate.query("select c.*, u.NAME, u.EMAIL from connections c JOIN users u ON c.user_seq=u.seq where target_seq = ? and c.granted_at is not null order by seq desc",
                new Object[]{targetId.value()}, mapper);
    }

    @Override
    public List<Connection> findUngrantedConnectionsByTargetId(Id<User, Long> targetId) {
        return jdbcTemplate.query("select c.*, u.NAME, u.EMAIL from connections c JOIN users u ON c.user_seq=u.seq where target_seq = ? and c.granted_at is null order by seq desc",
                new Object[]{targetId.value()}, mapper);
    }

    @Override
    public void grant(Connection connection) {
        jdbcTemplate.update(
                "UPDATE connections SET granted_at=? WHERE seq=?",
                timestampOf(connection.getGrantedAt().orElse(null)),
                connection.getSeq()
        );
    }

    @Override
    public Connection delete(Connection connection) {
        jdbcTemplate.update(
                "DELETE FROM connections WHERE seq=?",
                connection.getSeq()
        );
        return connection;
    }


    static RowMapper<Connection> mapper = (rs, rowNum) -> new Connection.Builder()
            .seq(rs.getLong("seq"))
            .userId(Id.of(User.class, rs.getLong("user_seq")))
            .targetId(Id.of(User.class, rs.getLong("target_seq")))
            .grantedAt(dateTimeOf(rs.getTimestamp("granted_at")))
            .createAt(dateTimeOf(rs.getTimestamp("create_at")))
            .from(new From(rs.getString("email"), rs.getString("name")))
            .build();
}
