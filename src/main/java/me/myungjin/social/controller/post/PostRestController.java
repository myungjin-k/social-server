package me.myungjin.social.controller.post;

import me.myungjin.social.configure.support.Pageable;
import me.myungjin.social.controller.ApiResult;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Comment;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.post.Writer;
import me.myungjin.social.model.user.User;
import me.myungjin.social.security.JwtAuthentication;
import me.myungjin.social.service.post.CommentService;
import me.myungjin.social.service.post.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static me.myungjin.social.controller.ApiResult.OK;

@RestController
@RequestMapping("api")
public class PostRestController {

  private final PostService postService;

  private final CommentService commentService;

  public PostRestController(PostService postService, CommentService commentService) {
    this.postService = postService;
    this.commentService = commentService;
  }

  @PostMapping(path = "post")
  public ApiResult<Post> posting(
    @AuthenticationPrincipal JwtAuthentication authentication,
    @RequestBody PostingRequest request
  ) {
    return OK(
      postService.write(request.newPost(authentication.id, new Writer(authentication.email, authentication.name)))
    );
  }

  @GetMapping(path = "user/{userId}/post/list")
  public ApiResult<List<Post>> posts(
    @AuthenticationPrincipal JwtAuthentication authentication,
    @PathVariable Long userId,
    Pageable pageable
  ) {
    return OK(
      postService.findAll(Id.of(User.class, userId), authentication.id, pageable.offset(), pageable.limit())
    );
  }

  @PatchMapping(path = "user/{userId}/post/{postId}")
  public ApiResult<Post> modify(
          @AuthenticationPrincipal JwtAuthentication authentication,
          @PathVariable Long userId,
          @PathVariable Long postId,
          @RequestBody Map<String, String> modified
  ) {
    return OK(
            postService.findById(Id.of(Post.class, postId), authentication.id, Id.of(User.class, userId))
                    .map(post -> {
                              post.modify(modified.get("contents"));
                              postService.modify(post);
                              return post;
                    }).orElseThrow(() -> new NotFoundException(Post.class, Id.of(Post.class, postId), Id.of(User.class, userId)))
    );
  }

  @PatchMapping(path = "user/{userId}/post/{postId}/like")
  public ApiResult<Post> like(
    @AuthenticationPrincipal JwtAuthentication authentication,
    @PathVariable Long userId,
    @PathVariable Long postId
  ) {
    return OK(
      postService.like(Id.of(Post.class, postId), Id.of(User.class, userId), authentication.id)
        .orElseThrow(() -> new NotFoundException(Post.class, Id.of(Post.class, postId), Id.of(User.class, userId)))
    );
  }

  @PostMapping(path = "user/{userId}/post/{postId}/comment")
  public ApiResult<Comment> comment(
    @AuthenticationPrincipal JwtAuthentication authentication,
    @PathVariable Long userId,
    @PathVariable Long postId,
    @RequestBody CommentRequest request
  ) {
    return OK(
      commentService.write(
        Id.of(Post.class, postId),
        Id.of(User.class, userId),
        authentication.id,
        request.newComment(
          authentication.id,
          Id.of(Post.class, postId),
          new Writer(authentication.email, authentication.name)
        )
      )
    );
  }

  @GetMapping(path = "user/{userId}/post/{postId}/comment/list")
  public ApiResult<List<Comment>> comments(
    @AuthenticationPrincipal JwtAuthentication authentication,
    @PathVariable Long userId,
    @PathVariable Long postId
  ) {
    return OK(
      commentService.findAll(Id.of(Post.class, postId), Id.of(User.class, userId), authentication.id)
    );
  }

  @PatchMapping(path = "user/{userId}/post/{postId}/comment/{commentId}")
  public ApiResult<Comment> modifyComments(
          @AuthenticationPrincipal JwtAuthentication authentication,
          @PathVariable Long userId,
          @PathVariable Long postId,
          @PathVariable Long commentId,
          @RequestBody Map<String, String> modified
  ) {
    return OK(
            commentService.findById(Id.of(Post.class, postId), Id.of(User.class, userId),  authentication.id, Id.of(Comment.class, commentId))
                    .map(comment -> {
                      comment.modify(modified.get("contents"));
                      commentService.modify(comment);
                      return comment;
                    }).orElseThrow(() -> new NotFoundException(Comment.class, userId, postId, authentication.id, commentId))
    );
  }

  @DeleteMapping(path = "user/{userId}/post/{postId}/comment/{commentId}")
  public ApiResult<Comment> deleteComment(
          @AuthenticationPrincipal JwtAuthentication authentication,
          @PathVariable Long userId,
          @PathVariable Long postId,
          @PathVariable Long commentId
  ) {
    return OK(
            commentService.remove(Id.of(Post.class, postId), Id.of(User.class, userId), authentication.id, Id.of(Comment.class, commentId))
    );
  }
}