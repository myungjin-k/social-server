package me.myungjin.social.configure;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zaxxer.hikari.HikariDataSource;
import me.myungjin.social.aws.S3Client;
import me.myungjin.social.security.Jwt;
import me.myungjin.social.util.MessageUtils;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

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
  public Jwt jwt(JwtTokenConfigure jwtTokenConfigure) {
    return new Jwt(jwtTokenConfigure.getIssuer(), jwtTokenConfigure.getClientSecret(), jwtTokenConfigure.getExpirySeconds());
  }

  @Bean
  public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
    MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
    MessageUtils.setMessageSourceAccessor(messageSourceAccessor);
    return messageSourceAccessor;
  }

  @Bean
  public AmazonS3 amazonS3Client(AwsConfigure awsConfigure) {
    return AmazonS3ClientBuilder.standard()
            .withRegion(Regions.fromName(awsConfigure.getRegion()))
            .withCredentials(
                    new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(
                                    awsConfigure.getAccessKey(),
                                    awsConfigure.getSecretKey())
                    )
            )
            .build();
  }

  @Bean
  public S3Client s3Client(AmazonS3 amazonS3, AwsConfigure awsConfigure) {
    return new S3Client(amazonS3, awsConfigure.getUrl(), awsConfigure.getBucketName());
  }

  @Bean
  public Jackson2ObjectMapperBuilder configureObjectMapper() {
    // Java time module
   // JavaTimeModule jtm = new JavaTimeModule();
    //jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder() {
      @Override
      public void configure(ObjectMapper objectMapper) {
        super.configure(objectMapper);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
      }
    };
    builder.serializationInclusion(JsonInclude.Include.NON_NULL);
    builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    //builder.modulesToInstall(jtm);
    return builder;
  }
}