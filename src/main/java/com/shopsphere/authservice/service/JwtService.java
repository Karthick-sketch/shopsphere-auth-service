package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.TokenProperties;
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

  private final TokenProperties tokenProperties;

  public String generateAccessToken(Long userId) {
    Long now = System.currentTimeMillis();
    return Jwts.builder()
      .claims(Map.of(CLAIM_TYPE, TOKEN_TYPE_ACCESS))
      .subject(userId.toString())
      .issuedAt(new Date(now))
      .expiration(new Date(now + tokenProperties.getAccessExpiration()))
      .signWith(getSigningKey())
      .compact();
  }

  public Boolean isAccessTokenValid(String token, Long userId) {
    return (
      TOKEN_TYPE_ACCESS.equals(extractTokenType(token)) &&
      extractId(token).equals(userId) &&
      !isTokenExpired(token)
    );
  }

  public Long extractId(String token) {
    return Long.valueOf(extractClaim(token, claim -> claim.getSubject()));
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(extractAllClaims(token));
  }

  private String extractTokenType(String token) {
    return extractClaim(token, claim -> claim.get(CLAIM_TYPE, String.class));
  }

  private Boolean isTokenExpired(String token) {
    return extractClaim(token, claim -> claim.getExpiration()).before(
      new Date()
    );
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(
      Decoders.BASE64.decode(tokenProperties.getAccessSecret())
    );
  }
}
