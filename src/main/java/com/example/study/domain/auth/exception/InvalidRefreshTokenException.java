package com.example.study.domain.auth.exception;

import com.example.study.global.exception.base.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends BusinessException {

  public InvalidRefreshTokenException() {
    super(HttpStatus.UNAUTHORIZED, "Refresh token is invalid.");
  }
}
