package me.myungjin.social.configure;

import com.zaxxer.hikari.HikariDataSource;
import me.myungjin.social.util.MessageUtils;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.MessageSourceAccessor;

import javax.sql.DataSource;

@Configuration
public class ServiceConfigure {

  @Bean
  @Profile("test")
  public DataSource testDataSource() {
    DataSourceBuilder factory = DataSourceBuilder
      .create()
      .driverClassName("org.h2.Driver")
      .url("jdbc:h2:mem:test_social;MODE=MYSQL;DB_CLOSE_DELAY=-1");
    //커넥션 풀이란 동시 접속자가 가질 수 있는 커넥션을 하나로 모아놓고 관리한다는 개념입니다. 누군가 접속하면 자신이 관리하는 풀에서 남아있는 커넥션을 제공합니다.
    //하지만 남아있는 커넥션이 없는 경우라면 해당 클라이언트는 대기 상태로 전환시킵니다. 그리고 커넥션이 다시 풀에 들어오면 대기 상태에 있는 클라이언트에게 순서대로 제공합니다.
    HikariDataSource dataSource = (HikariDataSource) factory.build();
    dataSource.setPoolName("TEST_H2_DB");
    dataSource.setMinimumIdle(1);
    dataSource.setMaximumPoolSize(1);
    return new Log4jdbcProxyDataSource(dataSource);
  }

  @Bean
  public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
    MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
    MessageUtils.setMessageSourceAccessor(messageSourceAccessor);
    return messageSourceAccessor;
  }
}