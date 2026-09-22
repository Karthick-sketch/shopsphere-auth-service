package com.shopsphere.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "token")
public class TokenProperties {

  private String accessSecret;
  private long accessExpiration;
  private long refreshExpiration;
  private String cookieName;
}
