package me.myungjin.social.repository.post;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Comment;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.post.Writer;
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
public class JdbcCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Comment save(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO comments(seq,user_seq,post_seq,contents,create_at) VALUES (null,?,?,?,?)", new String[]{"seq"});
            ps.setLong(1, comment.getUserId().value());
            ps.setLong(2, comment.getPostId().value());
            ps.setString(3, comment.getContents());
            ps.setTimestamp(4, timestampOf(comment.getCreateAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long generatedSeq = key != null ? key.longValue() : -1;
        return new Comment.Builder(comment)
          .seq(generatedSeq)
          .build();
    }

    @Override
    public void update(Comment comment) {
        jdbcTemplate.update(
          "UPDATE comments SET contents=? WHERE seq=?",
          comment.getContents(),
          comment.getSeq()
        );
    }

    @Override
    public void delete(Id<Comment, Long> commentId) {
        jdbcTemplate.update(
                "DELETE FROM comments WHERE seq = ?",
                commentId.value()
        );
    }

    @Override
    public Optional<Comment> findById(Id<Comment, Long> commentId, Id<User, Long> commentWriterId) {
        List<Comment> results = jdbcTemplate.query(
          "SELECT c.*,u.email,u.name FROM comments c JOIN users u ON c.user_seq=u.seq AND c.user_seq = ? WHERE c.seq=?",
          new Object[]{commentWriterId.value(), commentId.value()},
          mapper
        );
        return ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public List<Comment> findAll(Id<Post, Long> postId) {
        return jdbcTemplate.query(
          "SELECT c.*,u.email,u.name FROM comments c JOIN users u ON c.user_seq=u.seq WHERE c.post_seq=? ORDER BY c.seq DESC",
          new Object[]{postId.value()},
          mapper
        );
    }

    @Override
    public int countCommentsFromOthers(Id<Post, Long> postId, Id<User, Long> postWriterId) {
        Optional<Integer> result = ofNullable(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM comments c WHERE c.post_seq=? AND c.user_seq != ?",
                new Object[]{postId.value(), postWriterId.value()},
                (rs, rowNum) -> rs.getInt(1)
        ));

        return result.orElse(0);
    }

    static RowMapper<Comment> mapper = (rs, rowNum) -> new Comment.Builder()
      .seq(rs.getLong("seq"))
      .userId(Id.of(User.class, rs.getLong("user_seq")))
      .postId(Id.of(Post.class, rs.getLong("post_seq")))
      .contents(rs.getString("contents"))
      .writer(new Writer(rs.getString("email"), rs.getString("name")))
      .createAt(dateTimeOf(rs.getTimestamp("create_at")))
      .build();

}