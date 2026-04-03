package com.example.study.domain.user.exception;

import com.example.study.global.exception.base.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super(HttpStatus.NOT_FOUND, "User does not exist.");
  }
}
