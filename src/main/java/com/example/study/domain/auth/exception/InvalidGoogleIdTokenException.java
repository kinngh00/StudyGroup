package com.example.study.domain.auth.exception;

import com.example.study.global.exception.base.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidGoogleIdTokenException extends BusinessException {

  public InvalidGoogleIdTokenException() {
    super(HttpStatus.UNAUTHORIZED, "Google id token is invalid.");
  }
}
