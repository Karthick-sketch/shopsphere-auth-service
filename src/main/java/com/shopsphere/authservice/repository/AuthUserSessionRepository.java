package com.shopsphere.authservice.repository;

import com.shopsphere.authservice.entity.AuthUserSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserSessionRepository
  extends JpaRepository<AuthUserSession, Long>
{
  Optional<AuthUserSession> findByRefreshToken(String refreshToken);
}
