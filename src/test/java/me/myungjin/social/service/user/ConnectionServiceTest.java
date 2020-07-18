package me.myungjin.social.service.user;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.From;
import me.myungjin.social.model.user.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConnectionServiceTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private ConnectionService connectionService;

  private Id<User, Long> userId;

  private Id<User, Long> targetId;


  @BeforeAll
  void setUp() {
    userId = Id.of(User.class, 4L);
    userId = Id.of(User.class, 2L);
  }

  @Test
  @Order(1)
  void 친구_추가_요청을_한다_승인은_되지_않음() {
    Id<User, Long> userId = Id.of(User.class, 4L);
    Id<User, Long> targetId = Id.of(User.class, 2L);
    From from = new From("mjkim@gmail.com", "mjkim");
    Connection newConnection = connectionService.addConnection(userId, targetId, from).orElse(null);
    assertThat(newConnection, is(notNullValue()));
    assertThat(newConnection.getUserId(), is(userId));
    assertThat(newConnection.getTargetId(), is(targetId));
    assertThat(newConnection.getFrom(), is(from));
    log.info("Requested connection: {}", newConnection);
  }


  @Test
  @Order(2)
  void 승인하지_않은_친구_리스트를_가져온다() {
    Id<User, Long> userId = Id.of(User.class, 4L);
    Id<User, Long> targetId = Id.of(User.class, 2L);
    List<Connection> resultList = connectionService.findUngrantedConnections(targetId);
    assertThat(resultList, is(notNullValue()));
    assertThat(resultList.size(), is(1));
    assertThat(resultList.get(0).getTargetId(), is(targetId));
    assertThat(resultList.get(0).getUserId(), is(userId));
  }
}