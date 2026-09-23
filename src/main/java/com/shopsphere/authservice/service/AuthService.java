package com.shopsphere.authservice.service;

import com.shopsphere.authservice.dto.LoginRequest;
import com.shopsphere.authservice.dto.RegisterRequest;
import com.shopsphere.authservice.dto.Tokens;
import com.shopsphere.authservice.entity.*;
import com.shopsphere.authservice.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public Tokens register(RegisterRequest credential) {
    User user = userService.createUser(credential);
    return generateTokens(user);
  }

  public Tokens login(LoginRequest credential) {
    User user = userService.validateLogin(credential);
    return generateTokens(user);
  }

  public Tokens generateAccessToken(String refreshToken) {
    UserSession userSession = refreshTokenService.findByRefreshToken(
      refreshToken
    );

    if (!refreshTokenService.isValidRefreshToken(userSession)) {
      throw new InvalidTokenException();
    }

    return generateTokens(userSession);
  }

  public void logout(String refreshToken) {
    refreshTokenService.revokeRefreshToken(refreshToken);
  }

  private Tokens generateTokens(User user) {
    return new Tokens(
      jwtService.generateAccessToken(user.getId(), user.getRole()),
      refreshTokenService.generateRefreshToken(user)
    );
  }

  private Tokens generateTokens(UserSession userSession) {
    User user = userSession.getUser();
    return new Tokens(
      jwtService.generateAccessToken(user.getId(), user.getRole()),
      refreshTokenService.generateRefreshToken(userSession)
    );
  }
}
