package com.shopsphere.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientCredentialsRequest {

  private String clientId;
  private String clientSecret;
}
