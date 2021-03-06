package me.myungjin.social.controller.event.listener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.myungjin.social.controller.event.CommentCreateEvent;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.error.NotNotifiedException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.notification.NotificationService;
import me.myungjin.social.service.post.CommentService;
import me.myungjin.social.service.post.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentCreateEventListener implements AutoCloseable {

    private Logger log = LoggerFactory.getLogger(CommentCreateEventListener.class);

    private EventBus eventBus;

    private final NotificationService notificationService;

    private final PostService postService;

    private final CommentService commentService;

    public CommentCreateEventListener(EventBus eventBus, NotificationService notificationService, PostService postService, CommentService commentService) {
        this.eventBus = eventBus;
        this.notificationService = notificationService;
        eventBus.register(this);
        this.postService = postService;
        this.commentService = commentService;
    }

    @Subscribe
    public void handleCommentCreateEvent(CommentCreateEvent event) throws Exception {
        Id<Post, Long> postId = event.getPostId();
        Id<User, Long> postWriterId = event.getPostWriterId();
        Id<User, Long> commentWriterId = event.getCommentWriterId();
        Post post = postService.findById(postId, postWriterId, commentWriterId).orElseThrow(() -> new NotFoundException(Post.class, event));
        int commentCnt = commentService.countCommentsFromOthers(postId, postWriterId, postWriterId);
        log.info("{} writed a new comment on post {} !", commentWriterId, postId);

        try {
            log.info("Try to send push for {}", event);
            PushMessage pushMessage = new PushMessage(
                    "[" + post.getContents() +"] got (" + commentCnt + ") comments!",

                    //"[" + post.getContents() +"] got new comment!",
                    "user/" + postWriterId.value() + "/post/" + postId.value() + "/comment",
                    "Please check new comment"
            );
            notificationService.save(new Noti(postWriterId, pushMessage.getTitle(), pushMessage.getClickTarget()));
            notificationService.notifyUser(postWriterId, pushMessage);
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
