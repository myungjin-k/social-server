package me.myungjin.social.repository.post;

import me.myungjin.social.model.commons.Id;
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
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Post save(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO posts(seq,user_seq,contents,post_image_url,like_count,comment_count,create_at) VALUES (null,?,?,?,?,?,?)", new String[]{"seq"});
            ps.setLong(1, post.getUserId().value());
            ps.setString(2, post.getContents());
            ps.setString(3, post.getPostImageUrl().orElse(null));
            ps.setInt(4, post.getLikes());
            ps.setInt(5, post.getComments());
            ps.setTimestamp(6, timestampOf(post.getCreateAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        long generatedSeq = key != null ? key.longValue() : -1;
        return new Post.Builder(post)
          .seq(generatedSeq)
          .build();
    }

    @Override
    public void update(Post post) {
        jdbcTemplate.update(
          "UPDATE posts SET contents=?,post_image_url=?,like_count=?,comment_count=? WHERE seq=?",
          post.getContents(),
          post.getPostImageUrl().orElse(null),
          post.getLikes(),
          post.getComments(),
          post.getSeq()
        );
    }

    @Override
    public void delete(Id<Post, Long> postId) {
        jdbcTemplate.update(
                "DELETE FROM posts WHERE seq = ?",
                postId.value()
        );
    }

    @Override
    public Optional<Post> findById(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId) {
        List<Post> results = jdbcTemplate.query(
          "SELECT " +
            "p.*,u.email,u.name,ifnull(l.seq,false) as likesOfMe " +
            "FROM " +
            "posts p JOIN users u ON p.user_seq=u.seq LEFT OUTER JOIN likes l ON p.seq=l.post_seq AND l.user_seq=? " +
            "WHERE " +
            "p.seq=? AND p.user_seq=?",
          new Object[]{userId.value(), postId.value(), writerId.value()},
          mapper
        );
        return ofNullable(results.isEmpty() ? null : results.get(0));
    }

    @Override
    public List<Post> findAll(Id<User, Long> writerId, Id<User, Long> userId, long offset, int limit) {
        return jdbcTemplate.query(
          "SELECT " +
            "p.*,u.email,u.name,ifnull(l.seq,false) as likesOfMe " +
            "FROM " +
            "posts p JOIN users u ON p.user_seq=u.seq LEFT OUTER JOIN likes l ON p.seq=l.post_seq AND l.user_seq=? " +
            "WHERE " +
            "p.user_seq=? " +
            "ORDER BY " +
            "p.seq DESC " +
            "LIMIT ? OFFSET ?",
          new Object[]{userId.value(), writerId.value(), limit, offset},
          mapper
        );
    }

    static RowMapper<Post> mapper = (rs, rowNum) -> new Post.Builder()
      .seq(rs.getLong("seq"))
      .userId(Id.of(User.class, rs.getLong("user_seq")))
      .contents(rs.getString("contents"))
      .postImageUrl(rs.getString("post_image_url"))
      .likes(rs.getInt("like_count"))
      .likesOfMe(rs.getBoolean("likesOfMe"))
      .comments(rs.getInt("comment_count"))
      .writer(new Writer(rs.getString("email"), rs.getString("name")))
      .createAt(dateTimeOf(rs.getTimestamp("create_at")))
      .build();

}