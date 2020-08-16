package me.myungjin.social.service.post;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import me.myungjin.social.aws.S3Client;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.AttachedFile;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;
import me.myungjin.social.repository.post.PostLikeRepository;
import me.myungjin.social.repository.post.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

@Service
public class PostService {

  private final PostRepository postRepository;

  private final PostLikeRepository postLikeRepository;

  private final S3Client s3Client;

  private Logger log = LoggerFactory.getLogger(PostService.class);

  public PostService(PostRepository postRepository, PostLikeRepository postLikeRepository, S3Client s3Client) {
    this.postRepository = postRepository;
    this.postLikeRepository = postLikeRepository;
    this.s3Client = s3Client;
  }

  private Optional<String> uploadPostImage(AttachedFile postFile) {
    String postImageUrl = null;
    if (postFile != null) {
      String key = postFile.randomName("posts", "jpeg");
      try {
        postImageUrl = s3Client.upload(postFile.inputStream(), postFile.length(), key, postFile.getContentType(), null);
      } catch (AmazonS3Exception e) {
        log.warn("Amazon S3 error (key: {}): {}", key, e.getMessage(), e);
      }
    }
    return ofNullable(postImageUrl);
  }

  @Transactional
  public Post write(Post newPost, AttachedFile attachedFile) {
    Post post = new Post.Builder(newPost)
            .postImageUrl(uploadPostImage(attachedFile).orElse(null))
            .build();
    return save(post);
  }

  @Transactional
  public Post modify(Id<Post, Long> postId, Id<User, Long> userId, Id<User, Long> writerId, String contents, AttachedFile attachedFile) {
    return findById(postId, userId, writerId)
            .map(post -> {
              post.modify(contents, uploadPostImage(attachedFile).orElse(null));
              update(post);
              return post;
            }).orElseThrow(() -> new NotFoundException(Post.class, Id.of(Post.class, postId), Id.of(User.class, userId)));
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

  @Transactional(readOnly = true)
  public List<Post> feed(Id<User, Long> userId, long offset, int limit){
    checkNotNull(userId, "userId must be provided.");
    if(offset < 0)
      offset = 0;
    if(limit < 1 || limit > 5)
      limit = 5;
    return postRepository.findByConnection(userId, offset, limit);
  }

  @Transactional(readOnly = true)
  public List<Post> search(Id<User, Long> userId, String words, long offset, int limit){
    checkNotNull(userId, "userId must be provided.");
    checkNotNull(words, "words must be provided.");
    if(offset < 0)
      offset = 0;
    if(limit < 1 || limit > 5)
      limit = 5;
    return postRepository.findByContents(userId, words, offset, limit);
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