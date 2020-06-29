package me.myungjin.social.service.user;

import me.myungjin.social.model.user.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


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
  void 사용자를_추가한다() {
    User user = userService.join(name, email, password);
    assertThat(user, is(notNullValue()));
    assertThat(user.getSeq(), is(notNullValue()));
    assertThat(user.getEmail(), is(email));
    log.info("Inserted user: {}", user);
  }

}