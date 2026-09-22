package com.shopsphere.authservice.repository;

import com.shopsphere.authservice.entity.UserSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository
  extends JpaRepository<UserSession, Long>
{
  Optional<UserSession> findByRefreshToken(String refreshToken);
}
