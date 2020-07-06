package me.myungjin.social.controller.event.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.myungjin.social.controller.event.CommentCreateEvent;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.error.NotNotifiedException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.notification.NotificationService;
import me.myungjin.social.service.post.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentCreateEventListener implements AutoCloseable {

    private Logger log = LoggerFactory.getLogger(CommentCreateEventListener.class);

    private EventBus eventBus;

    private final NotificationService notificationService;

    private final PostService postService;

    public CommentCreateEventListener(EventBus eventBus, NotificationService notificationService, PostService postService) {
        this.eventBus = eventBus;
        this.notificationService = notificationService;
        eventBus.register(this);
        this.postService = postService;
    }

    @Subscribe
    public void handleCommentCreateEvent(CommentCreateEvent event) throws Exception {
        Id<Post, Long> postId = event.getPostId();
        Id<User, Long> postWriterId = event.getPostWriterId();
        Id<User, Long> commentWriterId = event.getCommentWriterId();
        Post post = postService.findById(postId, postWriterId, commentWriterId).orElseThrow(() -> new NotFoundException(Post.class, event));
        log.info("{} writed a new comment on post {} !", commentWriterId, postId);

        try {
            log.info("Try to send push for {}", event);
            notificationService.notifyUser(postWriterId,
                    new PushMessage(
                            "[" + post.getContents() +"] commented!",
                            "user/" + postWriterId.value() + "/post/" + postId.value() + "/comment",
                            "Please check new comment"
                    ));
            throw new IllegalArgumentException("test");
        } catch (Exception e) {
            log.error("Got error while handling event CommentCreateEvent " + event.toString(), e);
            //e.printStackTrace();
            throw new NotNotifiedException(CommentCreateEvent.class, e.getMessage(), event);
        }
    }

    @Override
    public void close() throws Exception {
        eventBus.unregister(this);
    }
}
