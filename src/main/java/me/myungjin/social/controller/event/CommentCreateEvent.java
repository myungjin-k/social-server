package me.myungjin.social.controller.event;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CommentCreateEvent {

    private final Id<Post, Long> postId;

    private final Id<User, Long> postWriterId;

    private final Id<User, Long> commentWriterId;

    public CommentCreateEvent(Id<Post, Long> postId, Id<User, Long> postWriterId, Id<User, Long> commentWriterId) {
        this.postId = postId;
        this.postWriterId = postWriterId;
        this.commentWriterId = commentWriterId;
    }

    public Id<Post, Long> getPostId() {
        return postId;
    }

    public Id<User, Long> getPostWriterId() {
        return postWriterId;
    }

    public Id<User, Long> getCommentWriterId() {
        return commentWriterId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("postId", postId)
                .append("postWriterId", postWriterId)
                .append("commentWriterId", commentWriterId)
                .toString();
    }
}
