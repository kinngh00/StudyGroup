package StudyGroup.StudyGroup.global.exception;

import StudyGroup.StudyGroup.global.response.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponseDto<Void>> customExceptionHandler(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();
    int status = errorCode.getStatus().value();

    return ResponseEntity.status(status)
        .body(ApiResponseDto.error(status, errorCode.name(), e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto<Void>> validationExceptionHandler(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldError() == null
        ? ErrorCode.INVALID_REQUEST.getMessage()
        : e.getBindingResult().getFieldError().getDefaultMessage();

    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus().value())
        .body(ApiResponseDto.error(
            ErrorCode.INVALID_REQUEST.getStatus().value(),
            ErrorCode.INVALID_REQUEST.name(),
            message
        ));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDto<Void>> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e) {
    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus().value())
        .body(ApiResponseDto.error(
            ErrorCode.INVALID_REQUEST.getStatus().value(),
            ErrorCode.INVALID_REQUEST.name(),
            "요청 본문 형식이 올바르지 않습니다."
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto<Void>> exceptionHandler(Exception e) {
    log.error("Unhandled exception occurred", e);
    return customExceptionHandler(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
  }
}
