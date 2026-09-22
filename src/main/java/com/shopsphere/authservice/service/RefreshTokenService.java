package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.TokenProperties;
import com.shopsphere.authservice.entity.*;
import com.shopsphere.authservice.repository.UserSessionRepository;
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

  private final UserSessionRepository userSessionRepository;

  public String generateRefreshToken(User user) {
    String token = generateRefreshToken();
    UserSession userSession = buildUserSession(token, user);
    userSessionRepository.save(userSession);
    return token;
  }

  public String generateRefreshToken(UserSession userSession) {
    revokeRefreshToken(userSession);
    String token = generateRefreshToken();
    UserSession newUserSession = buildUserSession(token, userSession.getUser());
    userSessionRepository.save(newUserSession);
    return token;
  }

  public UserSession findByRefreshToken(String refreshToken) {
    return userSessionRepository
      .findByRefreshToken(hashRefreshToken(refreshToken))
      .orElseThrow(() -> new RuntimeException("Invalid token"));
  }

  public boolean isValidRefreshToken(UserSession userSession) {
    return (
      userSession != null &&
      !userSession.getIsRevoked() &&
      userSession.getExpiresAt().isAfter(LocalDateTime.now())
    );
  }

  public void revokeRefreshToken(String refreshToken) {
    UserSession userSession = findByRefreshToken(refreshToken);
    if (!isValidRefreshToken(userSession)) {
      throw new RuntimeException("Invalid token");
    }
    revokeRefreshToken(userSession);
  }

  private void revokeRefreshToken(UserSession userSession) {
    userSession.setIsRevoked(true);
    userSessionRepository.save(userSession);
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

  private UserSession buildUserSession(String token, User user) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiresAt = now.plusMinutes(
      tokenProperties.getRefreshExpiration()
    );
    return UserSession.builder()
      .refreshToken(hashRefreshToken(token))
      .createdAt(now)
      .expiresAt(expiresAt)
      .user(user)
      .build();
  }
}
