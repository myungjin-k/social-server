package me.myungjin.social.service.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.user.ConnectionService;
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
public class NotificationServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private NotificationService notificationService;

    private Id<User, Long> userId;

    private Id<Noti, Long> notiId;


    @BeforeAll
    void setUp() {
        userId = Id.of(User.class, 4L);
        String endpoint = "https://fcm.googleapis.com/fcm/send/cNiXp-B8yo0:APA91bENYnL-wFD7Oln_ndV0i6cZMUVGmfyWFewvdfcLIxFGxPNo9gYeFzlsKbYHlgF3FcsjeCT68VOAg_KxxgDhnQzhTi-2D-EhAmph7BIYCOownRuCRryjFmt6ziZSy-KrT8os4qmv";
        String publicKey = "BO4zZrkWhitEWBv320ihgCf6s80jkbZDC0sh/aXXr47T284TZihDLHB9uktryWJcdkji+ON+JnbIW0b4bteqqKk=";
        String auth = "XccfTIo5HMBaCP6CLGUnIg==";
        //notificationService.subscribe(new Subscription(userId, endpoint, publicKey, auth));
    }

    @Test
    @Order(1)
    void 새로운_알림을_쌓는다() throws Exception {
        Noti noti = new Noti(userId, "Please check new test push", "user/" + userId.value() + "/post/list");
        Noti newNoti = notificationService.save(noti);

        assertThat(newNoti, is(notNullValue()));
        assertThat(newNoti.getUserId(), is(noti.getUserId()));

        log.info("Saved noti: {}", newNoti);
        notiId = Id.of(Noti.class, newNoti.getSeq());
    }


    @Test
    @Order(2)
    void 로그인_사용자의_알림_목록을_조회한다() throws Exception {
        List<Noti> notiList = notificationService.findAll(userId);

        assertThat(notiList, is(notNullValue()));
        assertThat(notiList.size(), is(1));

    }
    @Test
    @Order(3)
    void 로그인_사용자의_알림을_삭제한다() throws Exception {
        Noti deleted = notificationService.remove(notiId, userId);

        assertThat(deleted, is(notNullValue()));
        assertThat(deleted.getSeq(), is(notiId.value()));

        log.info("Deleted noti: {}", deleted);
    }
}
