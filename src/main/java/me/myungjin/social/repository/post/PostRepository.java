package me.myungjin.social.repository.post;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    void update(Post post);

    void delete(Id<Post, Long> postId);

    Optional<Post> findById(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId);

    List<Post> findAll(Id<User, Long> writerId, Id<User, Long> userId, long offset, int limit);

    List<Post> findByConnection(Id<User, Long> userId, long offset, int limit);

}