package com.shopsphere.authservice.service;

import com.shopsphere.authservice.dto.*;
import com.shopsphere.authservice.entity.UserCredential;
import com.shopsphere.authservice.repository.UserCredentialRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final PasswordEncoder passwordEncoder;

  private final UserCredentialRepository userCredentialRepository;

  private final JwtService jwtService;

  public AuthResponse register(RegisterRequest credential) {
    UserCredential userCredential = toUserCredential(credential);
    userCredential.setPassword(
      passwordEncoder.encode(userCredential.getPassword())
    );
    userCredential = userCredentialRepository.save(userCredential);
    return generateTokens(userCredential.getId());
  }

  public AuthResponse login(LoginRequest credential) {
    Optional<UserCredential> userCredential =
      userCredentialRepository.findByEmail(credential.getEmail());
    if (userCredential.isEmpty()) {
      throw new RuntimeException("User not found");
    }

    if (
      !passwordEncoder.matches(
        passwordEncoder.encode(credential.getPassword()),
        userCredential.get().getPassword()
      )
    ) {
      throw new RuntimeException("Invalid password");
    }

    return generateTokens(userCredential.get().getId());
  }

  public AuthResponse generateAccessToken(String refreshToken) {
    if (!jwtService.isRefreshToken(refreshToken)) {
      throw new RuntimeException("Invalid token");
    }

    Long credentialId;
    try {
      credentialId = jwtService.extractCredentialId(refreshToken);
    } catch (Exception ex) {
      throw new RuntimeException("Invalid token");
    }
    if (
      !jwtService.isTokenValid(refreshToken, credentialId) ||
      !userCredentialRepository.existsById(credentialId)
    ) {
      throw new RuntimeException("Invalid token");
    }

    return generateTokens(credentialId);
  }

  private AuthResponse generateTokens(Long userCredentialId) {
    return new AuthResponse(
      jwtService.generateAccessToken(userCredentialId),
      jwtService.generateRefreshToken(userCredentialId)
    );
  }

  private UserCredential toUserCredential(RegisterRequest credential) {
    return UserCredential.builder()
      .email(credential.getEmail())
      .password(credential.getPassword())
      .role(credential.getRole())
      .build();
  }
}
