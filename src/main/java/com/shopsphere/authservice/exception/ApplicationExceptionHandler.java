package com.shopsphere.authservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleException(RuntimeException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
