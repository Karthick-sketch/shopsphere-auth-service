package com.shopsphere.authservice.exception;

public class InvalidTokenException extends RuntimeException {

  public InvalidTokenException() {
    super("Invalid or expired token");
  }
}
