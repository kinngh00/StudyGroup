package com.example.study.global.exception;

import com.example.study.global.exception.base.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponseDto> handleBusinessException(
      BusinessException exception,
      HttpServletRequest httpServletRequest
  ) {
    HttpStatus httpStatus = exception.getHttpStatus();
    ErrorResponseDto errorResponseDto = ErrorResponseDto.of(
        httpStatus.value(),
        httpStatus.getReasonPhrase(),
        exception.getMessage(),
        httpServletRequest.getRequestURI()
    );
    return ResponseEntity.status(httpStatus).body(errorResponseDto);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception,
      HttpServletRequest httpServletRequest
  ) {
    String message = exception.getBindingResult().getFieldError() == null
        ? "Validation failed."
        : exception.getBindingResult().getFieldError().getDefaultMessage();

    ErrorResponseDto errorResponseDto = ErrorResponseDto.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        message,
        httpServletRequest.getRequestURI()
    );
    return ResponseEntity.badRequest().body(errorResponseDto);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
      IllegalArgumentException exception,
      HttpServletRequest httpServletRequest
  ) {
    ErrorResponseDto errorResponseDto = ErrorResponseDto.of(
        HttpStatus.BAD_REQUEST.value(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        exception.getMessage(),
        httpServletRequest.getRequestURI()
    );
    return ResponseEntity.badRequest().body(errorResponseDto);
  }
}
