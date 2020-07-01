package me.myungjin.social.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ClientTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private S3Client s3Client;

  @BeforeAll
  void setUp() {
    String region = "ap-northeast-2";
    String accessKey = "ACCESS_KEY";
    String secretKey = "SECRET_KEY";
    AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
      .withRegion(Regions.fromName(region))
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      .build();

    String url = "https://s3.ap-northeast-2.amazonaws.com";
    String bucketName = "myungjin-social";

    s3Client = new S3Client(amazonS3, url, bucketName);
  }

  @Test
  void S3_버킷으로_이미지를_업로드_및_삭제_할수있다() {
    URL testProfile = getClass().getResource("/feminist1.png");
    //MatcherAssert.assertThat(testProfile, Is.is(notNullValue()));

   assertThat(testProfile, is(notNullValue()));
    File file = new File(testProfile.getFile());
    String url = s3Client.upload(file);
   assertThat(url, is(notNullValue()));
    log.info("S3 bucket url: {}", url);

    s3Client.delete(url);
  }

}