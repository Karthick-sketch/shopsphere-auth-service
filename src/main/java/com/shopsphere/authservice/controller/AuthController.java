package com.shopsphere.authservice.controller;

import com.shopsphere.authservice.dto.*;
import com.shopsphere.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse register(@RequestBody RegisterRequest credential) {
    return authService.register(credential);
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest credential) {
    return authService.login(credential);
  }

  @PostMapping("/access")
  public AuthResponse getAccessToken(@RequestBody AccessTokenRequest request) {
    return authService.generateAccessToken(request.getRefreshToken());
  }
}
