package me.myungjin.social.repository.post;


import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;

public interface PostLikeRepository {

    void like(Id<User, Long> userId, Id<Post, Long> postId);

}