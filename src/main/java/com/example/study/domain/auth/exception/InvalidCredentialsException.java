package com.example.study.domain.auth.exception;

import com.example.study.global.exception.base.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {

  public InvalidCredentialsException() {
    super(HttpStatus.UNAUTHORIZED, "Email or password is invalid.");
  }
}
