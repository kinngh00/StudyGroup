package com.example.study.domain.auth.exception;

import com.example.study.global.exception.base.BusinessException;
import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends BusinessException {

  public RefreshTokenExpiredException() {
    super(HttpStatus.UNAUTHORIZED, "Refresh token has expired.");
  }
}
