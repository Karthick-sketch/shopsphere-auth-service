package com.shopsphere.authservice.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for registered service clients.
 * Clients are defined in application.properties as:
 *
 * service.clients.cart-service=secret-value
 * service.clients.order-service=secret-value
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "service")
public class ServiceClientProperties {

  /**
   * Map of clientId → clientSecret (plaintext).
   * e.g. service.clients.cart-service=my-secret
   */
  private Map<String, String> clients = Map.of();
}
