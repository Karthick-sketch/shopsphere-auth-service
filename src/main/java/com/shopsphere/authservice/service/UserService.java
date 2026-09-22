package com.shopsphere.authservice.service;

import com.shopsphere.authservice.dto.LoginRequest;
import com.shopsphere.authservice.dto.RegisterRequest;
import com.shopsphere.authservice.entity.User;
import com.shopsphere.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  public User findById(Long id) {
    return userRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public User findByEmail(String email) {
    return userRepository
      .findByEmail(email)
      .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public User validateLogin(LoginRequest request) {
    User user = findByEmail(request.getEmail());
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid email or password");
    }
    return user;
  }

  public User createUser(RegisterRequest credential) {
    User user = toUser(credential);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  private User toUser(RegisterRequest credential) {
    return User.builder()
      .email(credential.getEmail())
      .password(credential.getPassword())
      .build();
  }
}
