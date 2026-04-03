package com.example.study.domain.user.exception;

import com.example.study.global.exception.base.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BusinessException {

  public DuplicateEmailException() {
    super(HttpStatus.CONFLICT, "Email is already in use.");
  }
}
