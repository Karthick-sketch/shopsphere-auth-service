package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.TokenProperties;
import com.shopsphere.authservice.enums.UserRole;
import io.jsonwebtoken.Jwts;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private static final String CLAIM_TYPE = "type";
  private static final String CLAIM_ROLE = "role";
  private static final String TOKEN_TYPE_ACCESS = "access";

  private final TokenProperties tokenProperties;

  public String generateAccessToken(Long userId, UserRole role) {
    Long now = System.currentTimeMillis();
    return Jwts.builder()
      .claims(Map.of(CLAIM_TYPE, TOKEN_TYPE_ACCESS, CLAIM_ROLE, role))
      .subject(userId.toString())
      .issuedAt(new Date(now))
      .expiration(
        new Date(now + tokenProperties.getAccessExpiration() * 60 * 1000)
      )
      .signWith(getPrivateKey(), Jwts.SIG.RS256)
      .compact();
  }

  private PrivateKey getPrivateKey() {
    try {
      String key = Files.readString(
        Path.of(tokenProperties.getPrivateKeyPath())
      );
      String privateKeyContent = key
        .strip()
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s+", "");

      byte[] decodedKeyBytes = Base64.getDecoder().decode(privateKeyContent);

      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKeyBytes);

      KeyFactory factory = KeyFactory.getInstance("RSA");

      return factory.generatePrivate(keySpec);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
