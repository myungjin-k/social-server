package me.myungjin.social.configure;

import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.Security;

@Component
@ConfigurationProperties(prefix = "webpush")
public class PushConfigure {

  private String publicKey;
  private String privateKey;
  private static final String SUBJECT = "programmers-facebook";

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(String privateKey) {
    this.privateKey = privateKey;
  }

  @Bean
  public PushService pushService() throws GeneralSecurityException {
    // https://github.com/web-push-libs/webpush-java 참고
    Security.addProvider(new BouncyCastleProvider());
    return new PushService(publicKey, privateKey, SUBJECT);
  }

}
