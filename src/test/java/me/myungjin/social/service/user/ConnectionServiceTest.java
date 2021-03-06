package me.myungjin.social.service.user;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.From;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.notification.NotificationService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

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

  @Autowired
  private NotificationService notificationService;

  private Id<User, Long> userId;

  private Id<User, Long> targetId;


  @BeforeAll
  void setUp() {
    userId = Id.of(User.class, 4L);
    targetId = Id.of(User.class, 2L);
    String endpoint = "https://fcm.googleapis.com/fcm/send/cNiXp-B8yo0:APA91bENYnL-wFD7Oln_ndV0i6cZMUVGmfyWFewvdfcLIxFGxPNo9gYeFzlsKbYHlgF3FcsjeCT68VOAg_KxxgDhnQzhTi-2D-EhAmph7BIYCOownRuCRryjFmt6ziZSy-KrT8os4qmv";
    String publicKey = "BO4zZrkWhitEWBv320ihgCf6s80jkbZDC0sh/aXXr47T284TZihDLHB9uktryWJcdkji+ON+JnbIW0b4bteqqKk=";
    String auth = "XccfTIo5HMBaCP6CLGUnIg==";
    notificationService.subscribe(new Subscription(userId, endpoint, publicKey, auth));
    notificationService.subscribe(new Subscription(targetId, endpoint, publicKey, auth));
  }

  @Test
  @Order(1)
  void 친구_추가_요청을_한다_승인은_되지_않음() {
    From from = new From("mjkim@gmail.com", "mjkim");
    Connection newConnection = connectionService.add(userId, targetId, from).orElse(null);
    assertThat(newConnection, is(notNullValue()));
    assertThat(newConnection.getUserId(), is(userId));
    assertThat(newConnection.getTargetId(), is(targetId));
    assertThat(newConnection.getFrom(), is(from));
    log.info("Requested connection: {}", newConnection);
  }

  @Test
  @Order(2)
  void 나의_진행중인_친구_요청_리스트를_가져온다() {
    List<Connection> resultList = connectionService.findProceedingConnectionRequests(userId);
    assertThat(resultList, is(notNullValue()));
    assertThat(resultList.size(), is(1));
    assertThat(resultList.get(0).getTargetId(), is(targetId));
    assertThat(resultList.get(0).getUserId(), is(userId));
  }

  @Test
  @Order(3)
  void 내가_승인하지_않은_친구_리스트를_가져온다() {
    List<Connection> resultList = connectionService.findConnectionsNeedToBeGrantedByMe(targetId);
    assertThat(resultList, is(notNullValue()));
    assertThat(resultList.size(), is(1));
    assertThat(resultList.get(0).getTargetId(), is(targetId));
    assertThat(resultList.get(0).getUserId(), is(userId));
  }

  @Test
  @Order(4)
  void 친구요청을_수락한다() {
    Connection granted = connectionService.grant(userId, targetId);

    assertThat(granted, is(notNullValue()));
    assertThat(granted.getGrantedAt(), is(notNullValue()));
    log.info("Granted connection: {}", granted);
  }

  @Test
  @Order(4)
  void 나를_친구_추가한_사용자_리스트를_가져온다() {
    List<Connection> followers = connectionService.findFollowers(targetId);

    assertThat(followers, is(notNullValue()));
  }

  @Test
  @Order(5)
  void 친구구독을_취소한다() {
    Connection quited = connectionService.quit(userId, targetId);
    assertThat(quited, is(notNullValue()));

    Optional<Connection> check = connectionService.findById(userId, targetId);
    assertThat(check.isPresent(), is(false));
    log.info("Quited connection: {}", quited);
  }

}