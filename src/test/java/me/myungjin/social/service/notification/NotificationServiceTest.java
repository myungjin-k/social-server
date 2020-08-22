package me.myungjin.social.service.notification;

import me.myungjin.social.model.commons.Id;
import me.myungjin.social.model.notification.Noti;
import me.myungjin.social.model.notification.PushMessage;
import me.myungjin.social.model.notification.Subscription;
import me.myungjin.social.model.user.Connection;
import me.myungjin.social.model.user.From;
import me.myungjin.social.model.user.User;
import me.myungjin.social.service.user.ConnectionService;
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
public class NotificationServiceTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private NotificationService notificationService;

    private Id<User, Long> userId;


    @BeforeAll
    void setUp() {
        userId = Id.of(User.class, 4L);
        String endpoint = "https://fcm.googleapis.com/fcm/send/cNiXp-B8yo0:APA91bENYnL-wFD7Oln_ndV0i6cZMUVGmfyWFewvdfcLIxFGxPNo9gYeFzlsKbYHlgF3FcsjeCT68VOAg_KxxgDhnQzhTi-2D-EhAmph7BIYCOownRuCRryjFmt6ziZSy-KrT8os4qmv";
        String publicKey = "BO4zZrkWhitEWBv320ihgCf6s80jkbZDC0sh/aXXr47T284TZihDLHB9uktryWJcdkji+ON+JnbIW0b4bteqqKk=";
        String auth = "XccfTIo5HMBaCP6CLGUnIg==";
        notificationService.subscribe(new Subscription(userId, endpoint, publicKey, auth));
    }

    @Test
    @Order(1)
    void 새로운_알림을_쌓는다() throws Exception {
        notificationService.notifyUser(userId,
                new PushMessage(
                        userId + "got new test push!",
                        "user/" + userId.value() + "/post/list" ,
                        "Please check new test push"
                ));
        List<Noti> notiList = notificationService.findAll(userId);
        assertThat(notiList, is(notNullValue()));
        assertThat(notiList.size(), is(1));

        log.info("Save noti: {}", notiList.get(0));
    }


}
