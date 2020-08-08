package me.myungjin.social.controller.post;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.post.Writer;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PostingRequest {

  private String contents;

  protected PostingRequest() {}

  public void setContents(String contents) {
    this.contents = contents;
  }

  public String getContents() {
    return contents;
  }

  public Post newPost(Id<User, Long> userId, Writer writer) {
    return new Post(userId, writer, contents);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .append("contents", contents)
      .toString();
  }

}