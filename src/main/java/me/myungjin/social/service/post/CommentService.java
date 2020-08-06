package me.myungjin.social.service.post;

import com.google.common.eventbus.EventBus;
import me.myungjin.social.controller.event.CommentCreateEvent;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Comment;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.post.CommentRepository;
import me.myungjin.social.repository.post.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

@Service
public class CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final EventBus eventBus;


    public CommentService(PostRepository postRepository, CommentRepository commentRepository, EventBus eventBus) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.eventBus = eventBus;
    }

    @Transactional
    public Comment write(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId, Comment comment) {
        checkArgument(comment.getPostId().equals(postId), "comment.postId must equals postId");
        checkArgument(comment.getUserId().equals(userId), "comment.userId must equals userId");
        checkNotNull(comment, "comment must be provided.");

        return findPost(postId, postWriterId, userId)
          .map(post -> {
              post.incrementAndGetComments();
              postRepository.update(post);
              Comment newComment = save(comment);
              if(!newComment.getUserId().equals(postWriterId)){
                  eventBus.post(new CommentCreateEvent(postId, postWriterId, userId));
              }
              return newComment;
          }).orElseThrow(() -> new NotFoundException(Post.class, postId, userId));
    }

    @Transactional
    public Comment modify(Comment comment) {
        update(comment);
        return comment;
    }

    @Transactional
    public Comment remove(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId, Id<Comment, Long> commentId){
        return findById(postId, postWriterId, userId, commentId)
                .map(comment -> {
                    delete(commentId);
                    return comment;
                }).orElseThrow(() -> new NotFoundException(Comment.class, postId, postWriterId, userId, commentId));
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId) {
        return findPost(postId, postWriterId, userId)
          .map(post -> commentRepository.findAll(postId))
          .orElse(emptyList());
    }

    public Optional<Comment> findById(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId, Id<Comment, Long> commentId){
        checkNotNull(commentId, "commentId must be provided.");
        return findPost(postId, postWriterId, userId)
                .map(post -> commentRepository.findById(commentId, userId))
                .orElseThrow(() -> new NotFoundException(Post.class, postId, userId));
    }

    private Optional<Post> findPost(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> userId) {
        checkNotNull(postId, "postId must be provided.");
        checkNotNull(postWriterId, "postWriterId must be provided.");
        checkNotNull(userId, "userId must be provided.");

        return postRepository.findById(postId, postWriterId, userId);
    }


    private Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    private void update(Comment comment) {
        commentRepository.update(comment);
    }

    private void delete(Id<Comment, Long> commentId) {
        commentRepository.delete(commentId);
    }

}