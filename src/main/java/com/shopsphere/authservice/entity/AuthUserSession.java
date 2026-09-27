package com.shopsphere.authservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "auth_user_sessions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String refreshToken;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isRevoked = false;

  @ManyToOne
  @JoinColumn(name = "auth_user_id", nullable = false)
  private AuthUser authUser;
}
