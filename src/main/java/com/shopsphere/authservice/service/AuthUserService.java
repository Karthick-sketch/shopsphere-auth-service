package com.shopsphere.authservice.service;

import com.shopsphere.authservice.dto.LoginRequest;
import com.shopsphere.authservice.dto.RegisterRequest;
import com.shopsphere.authservice.entity.AuthUser;
import com.shopsphere.authservice.repository.AuthUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthUserService {

  private final PasswordEncoder passwordEncoder;

  private final AuthUserRepository authUserRepository;

  public AuthUser findById(Long id) {
    return authUserRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("AuthUser not found"));
  }

  public AuthUser findByEmail(String email) {
    return authUserRepository
      .findByEmail(email)
      .orElseThrow(() -> new RuntimeException("AuthUser not found"));
  }

  public AuthUser validateLogin(LoginRequest request) {
    AuthUser authUser = findByEmail(request.getEmail());
    if (
      !passwordEncoder.matches(request.getPassword(), authUser.getPassword())
    ) {
      throw new RuntimeException("Invalid email or password");
    }
    return authUser;
  }

  public AuthUser createAuthUser(RegisterRequest credential) {
    AuthUser authUser = toAuthUser(credential);
    authUser.setPassword(passwordEncoder.encode(authUser.getPassword()));
    return authUserRepository.save(authUser);
  }

  private AuthUser toAuthUser(RegisterRequest credential) {
    return AuthUser.builder()
      .name(credential.getName())
      .email(credential.getEmail())
      .password(credential.getPassword())
      .role(credential.getRole())
      .build();
  }
}
