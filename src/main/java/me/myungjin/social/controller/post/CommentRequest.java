package me.myungjin.social.controller.post;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Comment;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.post.Writer;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CommentRequest {

  private String contents;

  protected CommentRequest() {}

  public String getContents() {
    return contents;
  }

  public Comment newComment(Id<User, Long> userId, Id<Post, Long> postId, Writer writer) {
    return new Comment(userId, postId, writer, contents);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("contents", contents)
      .toString();
  }

}