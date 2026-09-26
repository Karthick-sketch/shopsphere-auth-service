package com.shopsphere.authservice.service;

import com.shopsphere.authservice.config.TokenProperties;
import com.shopsphere.authservice.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

  private final TokenProperties tokenProperties;

  public String createRefreshTokenCookie(String refreshToken) {
    return buildCookie(refreshToken, tokenProperties.getRefreshExpiration());
  }

  public String createExpiredRefreshTokenCookie() {
    return buildCookie("", 0L);
  }

  private String buildCookie(String token, Long maxAge) {
    return ResponseCookie.from(SecurityConstants.COOKIE_NAME, token)
      .httpOnly(true)
      .sameSite("Strict")
      .secure(false) // Not using SSL yet
      .path(SecurityConstants.COOKIE_PATH)
      .maxAge(maxAge)
      .build()
      .toString();
  }
}
