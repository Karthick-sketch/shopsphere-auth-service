package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.TokenProperties;
import com.shopsphere.authservice.enums.UserRole;
import com.shopsphere.authservice.util.KeyUtil;
import io.jsonwebtoken.Jwts;
import java.security.interfaces.RSAPrivateKey;
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
    Long expiration = now + tokenProperties.getAccessExpiration() * 60 * 1000;
    RSAPrivateKey privateKey = KeyUtil.loadPrivateKey(
      tokenProperties.getPrivateKeyPath()
    );

    return Jwts.builder()
      .claims(Map.of(CLAIM_TYPE, TOKEN_TYPE_ACCESS, CLAIM_ROLE, role))
      .subject(userId.toString())
      .issuedAt(new Date(now))
      .expiration(new Date(expiration))
      .signWith(privateKey, Jwts.SIG.RS256)
      .compact();
  }
}
