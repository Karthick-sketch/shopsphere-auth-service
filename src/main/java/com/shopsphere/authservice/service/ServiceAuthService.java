package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.ServiceClientProperties;
import com.shopsphere.authservice.config.TokenProperties;
import com.shopsphere.authservice.dto.ClientCredentialsRequest;
import com.shopsphere.authservice.dto.ServiceTokenResponse;
import com.shopsphere.authservice.util.KeyUtil;
import io.jsonwebtoken.Jwts;
import java.security.interfaces.RSAPrivateKey;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the client credentials grant flow for service-to-service
 * authentication. Validates service client credentials against
 * config properties and issues a JWT with a SERVICE role claim.
 */
@Service
@RequiredArgsConstructor
public class ServiceAuthService {

  private static final String CLAIM_TYPE = "type";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_SERVICE = "service";
  private static final String TOKEN_TYPE_SERVICE = "service";
  private static final String ROLE_SERVICE = "SERVICE";

  private final ServiceClientProperties serviceClientProperties;
  private final TokenProperties tokenProperties;

  public ServiceTokenResponse authenticateClient(
    ClientCredentialsRequest request
  ) {
    String expectedSecret = serviceClientProperties
      .getClients()
      .get(request.getClientId());

    if (
      expectedSecret == null ||
      !expectedSecret.equals(request.getClientSecret())
    ) {
      throw new IllegalArgumentException("Invalid client credentials");
    }

    long now = System.currentTimeMillis();
    long expiresInSeconds = tokenProperties.getAccessExpiration() * 60;
    long expirationMs = now + expiresInSeconds * 1000;

    RSAPrivateKey privateKey = KeyUtil.loadPrivateKey(
      tokenProperties.getPrivateKeyPath()
    );

    String token = Jwts.builder()
      .claims(
        Map.of(
          CLAIM_TYPE,
          TOKEN_TYPE_SERVICE,
          CLAIM_ROLE,
          ROLE_SERVICE,
          CLAIM_SERVICE,
          request.getClientId()
        )
      )
      .subject(request.getClientId())
      .issuedAt(new Date(now))
      .expiration(new Date(expirationMs))
      .signWith(privateKey, Jwts.SIG.RS256)
      .compact();

    return new ServiceTokenResponse(
      token,
      expiresInSeconds,
      "Bearer"
    );
  }
}
