package me.myungjin.social.service.post;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.post.Comment;
import me.myungjin.social.model.post.Post;
import me.myungjin.social.model.post.Writer;
import me.myungjin.social.model.user.User;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static me.myungjin.social.model.commons.AttachedFile.toAttachedFile;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostServiceTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private PostService postService;
  @Autowired
  private CommentService commentService;

  private Id<Post, Long> postId;

  private Id<User, Long> writerId;

  private Id<User, Long> userId;

  @BeforeAll
  void setUp() {
    postId = Id.of(Post.class, 1L);
    writerId = Id.of(User.class, 1L);
    userId = Id.of(User.class, 2L);
  }

  @Test
  @Order(1)
  void 포스트를_작성한다() throws IOException {
    Writer writer = new Writer("test00@gmail.com", "test");
    String contents = randomAlphabetic(40);
    URL testProfile = getClass().getResource("/test.jpg");
    File file = new File(testProfile.getFile());
    FileInputStream input = new FileInputStream(file);
    MultipartFile multipartFile =  new MockMultipartFile("file",
            file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    Post post = postService.write(new Post(writerId, writer, contents), toAttachedFile(multipartFile));
    assertThat(post, is(notNullValue()));
    assertThat(post.getSeq(), is(notNullValue()));
    assertThat(post.getContents(), is(contents));
    log.info("Written post: {}", post);
  }

  @Test
  @Order(2)
  void 포스트를_수정한다() {
    String contents = randomAlphabetic(40);
    Post modified = postService.modify(postId, writerId, userId, contents, null);
    assertThat(modified.getContents(), is(contents));
    assertThat(modified.getPostImageUrl().isPresent(), is(false));
    log.info("Modified post: {}", modified);
  }

  @Test
  @Order(3)
  void 포스트_목록을_조회한다() {
    List<Post> posts = postService.findAll(writerId, userId, 0, 20);
    assertThat(posts, is(notNullValue()));
    assertThat(posts.size(), is(4));
  }

  @Test
  @Order(4)
  void 포스트를_처음으로_좋아한다() {
    Post post;

    post = postService.findById(postId, writerId, userId).orElse(null);
    assertThat(post, is(notNullValue()));
    assertThat(post.isLikesOfMe(), is(false));

    int beforeLikes = post.getLikes();

    post = postService.like(postId, writerId, userId).orElse(null);
    assertThat(post, is(notNullValue()));
    assertThat(post.isLikesOfMe(), is(true));
    assertThat(post.getLikes(), is(beforeLikes + 1));
  }

  @Test
  @Order(5)
  void 포스트를_중복으로_좋아할수없다() {
    Post post;

    post = postService.findById(postId, writerId, userId).orElse(null);
    assertThat(post, is(notNullValue()));
    assertThat(post.isLikesOfMe(), is(true));

    int beforeLikes = post.getLikes();

    post = postService.like(postId, writerId, userId).orElse(null);
    assertThat(post, is(notNullValue()));
    assertThat(post.isLikesOfMe(), is(true));
    assertThat(post.getLikes(), is(beforeLikes));
  }

  @Test
  @Order(6)
  void 포스트를_삭제한다() {
    Post post = postService.findById(postId, writerId, userId).orElse(null);
    assertThat(post, is(notNullValue()));
    log.info("Post will be deleted : {}", post);

    postService.remove(postId, writerId, userId);
    post = postService.findById(postId, writerId, userId).orElse(null);
    assertThat(post == null, is(true));

    List<Comment> commentList = commentService.findAll(postId, writerId, userId);
    assertThat(commentList.isEmpty(), is(true));
  }

  @Test
  @Order(7)
  void 구독한_사용자의_포스트_목록을_조회한다() {
    List<Post> connectedPostList = postService.feed(userId, 0, 20);
    assertThat(connectedPostList, is(notNullValue()));
    assertThat(connectedPostList.get(0).getUserId().value(), is(1L));
    assertThat(connectedPostList.size(), is(3));
  }

  @Test
  @Order(8)
  void 포스트_내용을_검색한다() {
    List<Post> resultList = postService.search(userId, "first", 0, 20);
    assertThat(resultList, is(notNullValue()));
    assertThat(resultList.size(), is(2));
    log.info("Searched Post 1 : {}", resultList.get(0));
    log.info("Searched Post 2 : {}", resultList.get(1));
  }
}