package me.myungjin.social.repository.post;


import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Comment;
import me.myungjin.social.model.post.Post;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);

    void update(Comment comment);

    Optional<Comment> findById(Id<Comment, Long> commentId);

    List<Comment> findAll(Id<Post, Long> postId);

}