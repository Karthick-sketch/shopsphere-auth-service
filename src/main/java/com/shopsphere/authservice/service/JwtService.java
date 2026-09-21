package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private static final String CLAIM_TYPE = "type";
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final JwtProperties jwtProperties;

  public String generateAccessToken(Long userCredentialId) {
    return buildToken(
      Map.of(CLAIM_TYPE, TOKEN_TYPE_ACCESS),
      userCredentialId,
      jwtProperties.getAccessTokenExpiration()
    );
  }

  public String generateRefreshToken(Long userCredentialId) {
    return buildToken(
      Map.of(CLAIM_TYPE, TOKEN_TYPE_REFRESH),
      userCredentialId,
      jwtProperties.getRefreshTokenExpiration()
    );
  }

  public boolean isRefreshToken(String token) {
    return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
  }

  public boolean isTokenValid(String token, Long credentialId) {
    return (
      extractCredentialId(token).equals(credentialId) && !isTokenExpired(token)
    );
  }

  public Long extractCredentialId(String token) {
    return Long.valueOf(extractClaim(token, claim -> claim.getSubject()));
  }

  private String buildToken(
    Map<String, String> claims,
    Long credentialId,
    long expiration
  ) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
      .claims(claims)
      .subject(credentialId.toString())
      .issuedAt(new Date(now))
      .expiration(new Date(now + expiration))
      .signWith(getSigningKey())
      .compact();
  }

  private Claims exctractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(exctractAllClaims(token));
  }

  private String extractTokenType(String token) {
    return extractClaim(token, claim -> claim.get(CLAIM_TYPE, String.class));
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, claim -> claim.getExpiration()).before(
      new Date()
    );
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(
      Decoders.BASE64.decode(jwtProperties.getSecret())
    );
  }
}
