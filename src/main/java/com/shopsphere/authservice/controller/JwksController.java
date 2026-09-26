package com.shopsphere.authservice.controller;

import com.nimbusds.jose.jwk.JWKSet;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class JwksController {

  private final JWKSet jwkSet;

  @GetMapping("/.well-known/jwks.json")
  public Map<String, Object> getJwks() {
    return jwkSet.toJSONObject();
  }
}
