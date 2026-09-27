package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.TokenProperties;
import com.shopsphere.authservice.entity.*;
import com.shopsphere.authservice.exception.InvalidTokenException;
import com.shopsphere.authservice.repository.AuthUserSessionRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final TokenProperties tokenProperties;

  private final AuthUserSessionRepository authUserSessionRepository;

  public String generateRefreshToken(AuthUser authUser) {
    String token = generateRefreshToken();
    AuthUserSession authUserSession = buildAuthUserSession(token, authUser);
    authUserSessionRepository.save(authUserSession);
    return token;
  }

  public String generateRefreshToken(AuthUserSession authUserSession) {
    revokeRefreshToken(authUserSession);
    String token = generateRefreshToken();
    AuthUserSession newAuthUserSession = buildAuthUserSession(
      token,
      authUserSession.getAuthUser()
    );
    authUserSessionRepository.save(newAuthUserSession);
    return token;
  }

  public AuthUserSession findByRefreshToken(String refreshToken) {
    return authUserSessionRepository
      .findByRefreshToken(hashRefreshToken(refreshToken))
      .orElseThrow(() -> new InvalidTokenException());
  }

  public boolean isValidRefreshToken(AuthUserSession authUserSession) {
    return (
      authUserSession != null &&
      !authUserSession.getIsRevoked() &&
      authUserSession.getExpiresAt().isAfter(LocalDateTime.now())
    );
  }

  public void revokeRefreshToken(String refreshToken) {
    AuthUserSession authUserSession = findByRefreshToken(refreshToken);
    if (!isValidRefreshToken(authUserSession)) {
      throw new InvalidTokenException();
    }
    revokeRefreshToken(authUserSession);
  }

  private void revokeRefreshToken(AuthUserSession authUserSession) {
    authUserSession.setIsRevoked(true);
    authUserSessionRepository.save(authUserSession);
  }

  private String generateRefreshToken() {
    byte[] randomBytes = new byte[64];
    new SecureRandom().nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  private String hashRefreshToken(String token) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] hash = messageDigest.digest(
        token.getBytes(StandardCharsets.UTF_8)
      );
      return HexFormat.of().formatHex(hash);
    } catch (Exception e) {
      throw new RuntimeException("Failed to hash refresh token", e);
    }
  }

  private AuthUserSession buildAuthUserSession(
    String token,
    AuthUser authUser
  ) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiresAt = now.plusMinutes(
      tokenProperties.getRefreshExpiration()
    );
    return AuthUserSession.builder()
      .refreshToken(hashRefreshToken(token))
      .createdAt(now)
      .expiresAt(expiresAt)
      .authUser(authUser)
      .build();
  }
}
