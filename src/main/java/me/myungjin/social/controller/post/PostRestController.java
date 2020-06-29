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

}