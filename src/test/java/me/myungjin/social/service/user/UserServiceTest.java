package me.myungjin.social.service.user;

import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.model.commons.AttachedFile;
import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.ConnectedUser;
import me.myungjin.social.model.user.User;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.omg.CosNaming.NamingContextPackage.NotFound;
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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private UserService userService;

  private String name;

  private String email;

  private String password;


  @BeforeAll
  void setUp() {
    name = "test";
    email = "test@gmail.com";
    password = "1234";
  }

  @Test
  @Order(1)
  void 사용자를_추가한다() throws IOException {

    URL testProfile = getClass().getResource("/feminist1.png");
    File file = new File(testProfile.getFile());
    FileInputStream input = new FileInputStream(file);
    MultipartFile multipartFile =  new MockMultipartFile("file",
            file.getName(), "image/jpeg", IOUtils.toByteArray(input));
    User user = userService.join(name, email, password, AttachedFile.toAttachedFile(multipartFile));
    assertThat(user, is(notNullValue()));
    assertThat(user.getSeq(), is(notNullValue()));
    assertThat(user.getEmail(), is(email));

    log.info("Inserted user: {}", user);
  }

  @Test
  @Order(2)
  void 사용자를_이메일로_조회한다() {
    User user = userService.findByEmail(email).orElse(null);
    assertThat(user, is(notNullValue()));
    assertThat(user.getEmail(), is(email));
    log.info("Found by {}: {}", email, user);
  }

  @Test
  @Order(3)
  void 이메일로_로그인한다() {
    User user = userService.login(email, password);
    assertThat(user, is(notNullValue()));
    assertThat(user.getEmail(), is(email));
    assertThat(user.getLoginCount(), is(1));
    log.info("First login: {}", user);

    user = userService.login(email, password);
    assertThat(user, is(notNullValue()));
    assertThat(user.getEmail(), is(email));
    assertThat(user.getLoginCount(), is(2));
    log.info("Second login: {}", user);
  }

  @Test
  @Order(4)
  void 잘못된_비밀번호로_로그인을_할수없다() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> userService.login(email, "invalid password"));
  }

  @Test
  @Order(5)
  void 친구_목록을_가져온다() {
    List<ConnectedUser> connected = userService.findAllConnectedUser(Id.of(User.class, 1L));
    assertThat(connected, is(notNullValue()));
    assertThat(connected.size(), is(1));
  }

  @Test
  @Order(6)
  void 친구ID_목록을_가져온다() {
    List<Id<User, Long>> connectedIds = userService.findConnectedIds(Id.of(User.class, 1L));
    assertThat(connectedIds, is(notNullValue()));
    assertThat(connectedIds.size(), is(1));
    assertThat(connectedIds.get(0).value(), is(2L));
  }

  @Test
  @Order(7)
  void 사용자_이름과_프로필을_수정한다() throws IOException {

    URL testProfile = getClass().getResource("/feminist1.png");
    File file = new File(testProfile.getFile());
    FileInputStream input = new FileInputStream(file);
    MultipartFile multipartFile =  new MockMultipartFile("file",
            file.getName(), "image/jpeg", IOUtils.toByteArray(input));

    Id<User, Long> authId = Id.of(User.class, 1L);
    String newName = "newName";

    User user = userService.modify(authId, newName, AttachedFile.toAttachedFile(multipartFile))
            .orElseThrow(() -> new NotFoundException(User.class, authId.value()));

    assertThat(user, is(notNullValue()));
    assertThat(user.getName(), is(newName));
    assertThat(user.getProfileImageUrl(), is(notNullValue()));

    log.info("Modified user: {}", user);
  }
}