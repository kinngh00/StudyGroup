package SudyGroup.StudyGroup.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ExceptionResponseDto> customExceptionHandler(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();
    return ResponseEntity
        .status(errorCode.getStatusValue())
        .body(new ExceptionResponseDto(e));
  }
}
