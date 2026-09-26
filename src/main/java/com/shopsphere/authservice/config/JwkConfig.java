package com.shopsphere.authservice.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.shopsphere.authservice.util.KeyUtil;
import java.security.interfaces.RSAPublicKey;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class JwkConfig {

  private final TokenProperties tokenProperties;

  @Bean
  public RSAKey rsaKey() throws Exception {
    RSAPublicKey publicKey = KeyUtil.loadPublicKey(
      tokenProperties.getPublicKeyPath()
    );

    return new RSAKey.Builder(publicKey)
      .keyID(tokenProperties.getKeyID())
      .algorithm(JWSAlgorithm.RS256)
      .keyUse(KeyUse.SIGNATURE)
      .build();
  }

  @Bean
  public JWKSet jwkSet(RSAKey rsaKey) {
    return new JWKSet(rsaKey.toPublicJWK());
  }
}
