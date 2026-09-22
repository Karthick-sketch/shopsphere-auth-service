package com.shopsphere.authservice.controller;

import com.shopsphere.authservice.constants.SecurityConstants;
import com.shopsphere.authservice.dto.*;
import com.shopsphere.authservice.service.AuthService;
import com.shopsphere.authservice.service.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final CookieService cookieService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
    @RequestBody RegisterRequest credential
  ) {
    return buildAuthResponse(authService.register(credential));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
    @RequestBody LoginRequest credential
  ) {
    return buildAuthResponse(authService.login(credential));
  }

  @PostMapping("/access")
  public ResponseEntity<AuthResponse> getAccessToken(
    @CookieValue(
      name = SecurityConstants.COOKIE_REFRESH_TOKEN
    ) String refreshToken
  ) {
    if (refreshToken == null) {
      return ResponseEntity.badRequest().build();
    }
    return buildAuthResponse(authService.generateAccessToken(refreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(
    @CookieValue(
      name = SecurityConstants.COOKIE_REFRESH_TOKEN
    ) String refreshToken
  ) {
    if (refreshToken == null) {
      return ResponseEntity.badRequest().build();
    }
    authService.logout(refreshToken);
    String cookie = cookieService.createExpiredRefreshTokenCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).build();
  }

  private ResponseEntity<AuthResponse> buildAuthResponse(Tokens tokens) {
    return ResponseEntity.ok()
      .header(
        HttpHeaders.SET_COOKIE,
        cookieService.createRefreshTokenCookie(tokens.getRefreshToken())
      )
      .body(new AuthResponse(tokens.getAccessToken()));
  }
}
