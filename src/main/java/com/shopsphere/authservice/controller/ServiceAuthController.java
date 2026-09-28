package com.shopsphere.authservice.controller;

import com.shopsphere.authservice.dto.ClientCredentialsRequest;
import com.shopsphere.authservice.dto.ServiceTokenResponse;
import com.shopsphere.authservice.service.ServiceAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ServiceAuthController {

  private final ServiceAuthService serviceAuthService;

  @PostMapping("/token")
  public ResponseEntity<ServiceTokenResponse> getServiceToken(
    @RequestBody ClientCredentialsRequest request
  ) {
    return ResponseEntity.ok(
      serviceAuthService.authenticateClient(request)
    );
  }
}
