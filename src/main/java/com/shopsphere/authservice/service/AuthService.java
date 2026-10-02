package com.shopsphere.authservice.service;

import com.shopsphere.authservice.dto.AuthUserResponse;
import com.shopsphere.authservice.dto.LoginRequest;
import com.shopsphere.authservice.dto.RegisterRequest;
import com.shopsphere.authservice.dto.Tokens;
import com.shopsphere.authservice.entity.*;
import com.shopsphere.authservice.exception.InvalidTokenException;
import com.shopsphere.authservice.kafka.KafkaProducerService;
import com.shopsphere.authservice.kafka.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthUserService authUserService;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final KafkaProducerService kafkaProducerService;

  public Tokens register(RegisterRequest credential) {
    AuthUser authUser = authUserService.createAuthUser(credential);
    Tokens tokens = generateTokens(authUser);
    kafkaProducerService.sendUserCreatedEvent(toUserCreatedEvent(authUser));
    return tokens;
  }

  public Tokens login(LoginRequest credential) {
    AuthUser authUser = authUserService.validateLogin(credential);
    return generateTokens(authUser);
  }

  public Tokens generateAccessToken(String refreshToken) {
    AuthUserSession authUserSession = refreshTokenService.findByRefreshToken(
      refreshToken
    );

    if (!refreshTokenService.isValidRefreshToken(authUserSession)) {
      throw new InvalidTokenException();
    }

    return generateTokens(authUserSession);
  }

  public void logout(String refreshToken) {
    refreshTokenService.revokeRefreshToken(refreshToken);
  }

  private Tokens generateTokens(AuthUser authUser) {
    return new Tokens(
      jwtService.generateAccessToken(authUser.getId(), authUser.getRole()),
      refreshTokenService.generateRefreshToken(authUser),
      toAuthUserResponse(authUser)
    );
  }

  private Tokens generateTokens(AuthUserSession authUserSession) {
    AuthUser authUser = authUserSession.getAuthUser();
    return new Tokens(
      jwtService.generateAccessToken(authUser.getId(), authUser.getRole()),
      refreshTokenService.generateRefreshToken(authUserSession),
      toAuthUserResponse(authUser)
    );
  }

  private AuthUserResponse toAuthUserResponse(AuthUser authUser) {
    return new AuthUserResponse(
      authUser.getId(),
      authUser.getName(),
      authUser.getEmail(),
      authUser.getRole()
    );
  }

  private UserCreatedEvent toUserCreatedEvent(AuthUser authUser) {
    return new UserCreatedEvent(
      authUser.getId(),
      authUser.getName(),
      authUser.getEmail()
    );
  }
}
