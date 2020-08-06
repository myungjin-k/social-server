package me.myungjin.social.service.post;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.post.PostLikeRepository;
import me.myungjin.social.repository.post.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class PostService {

  private final PostRepository postRepository;

  private final PostLikeRepository postLikeRepository;

  public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository) {
    this.postRepository = postRepository;
    this.postLikeRepository = postLikeRepository;
  }

  @Transactional
  public Post write(Post post) {
    return save(post);
  }

  @Transactional
  public Post modify(Post post) {
    update(post);
    return post;
  }

  @Transactional
  public Optional<Post> like(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId) {
    return findById(postId, writerId, userId).map(post -> {
      if (!post.isLikesOfMe()) {
        post.incrementAndGetLikes();
        postLikeRepository.like(userId, postId);
        update(post);
      }
      return post;
    });
  }

  @Transactional
  public Post remove(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId) {
      return findById(postId, writerId, userId)
              .map(post -> {
                delete(postId);
                return post;
              }).orElseThrow(() -> new NotFoundException(Post.class, postId, writerId, userId));
  }

  @Transactional(readOnly = true)
  public Optional<Post> findById(Id<Post, Long> postId, Id<User, Long> writerId, Id<User, Long> userId) {
    checkNotNull(writerId, "writerId must be provided.");
    checkNotNull(postId, "postId must be provided.");
    checkNotNull(userId, "userId must be provided.");

    return postRepository.findById(postId, writerId, userId);
  }

  @Transactional(readOnly = true)
  public List<Post> findAll(Id<User, Long> writerId, Id<User, Long> userId, long offset, int limit) {
    checkNotNull(writerId, "writerId must be provided.");
    checkNotNull(userId, "userId must be provided.");
    if (offset < 0)
      offset = 0;
    if (limit < 1 || limit > 5)
      limit = 5;

    return postRepository.findAll(writerId, userId, offset, limit);
  }

  private Post save(Post post) {
    return postRepository.save(post);
  }

  private void update(Post post) {
    postRepository.update(post);
  }

  private void delete(Id<Post, Long> postId) {
    postRepository.delete(postId);
  }
}