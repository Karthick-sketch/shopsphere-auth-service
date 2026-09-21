package com.shopsphere.authservice.repository;

import com.shopsphere.authservice.entity.UserCredential;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository
  extends JpaRepository<UserCredential, Long>
{
  Optional<UserCredential> findByEmail(String email);
}
