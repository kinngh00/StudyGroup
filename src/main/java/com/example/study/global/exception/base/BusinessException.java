package com.example.study.global.exception.base;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

  private final HttpStatus httpStatus;

  protected BusinessException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
