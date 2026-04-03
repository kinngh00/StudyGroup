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

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponseDto<Void>> businessExceptionHandler(BusinessException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    int status = errorCode.getStatus().value();

    return ResponseEntity.status(status)
        .body(ApiResponseDto.error(status, errorCode.name(), exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto<Void>> validationExceptionHandler(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldError() == null
        ? ErrorCode.INVALID_REQUEST.getMessage()
        : exception.getBindingResult().getFieldError().getDefaultMessage();

    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus().value())
        .body(ApiResponseDto.error(
            ErrorCode.INVALID_REQUEST.getStatus().value(),
            ErrorCode.INVALID_REQUEST.name(),
            message
        ));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDto<Void>> httpMessageNotReadableExceptionHandler() {
    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus().value())
        .body(ApiResponseDto.error(
            ErrorCode.INVALID_REQUEST.getStatus().value(),
            ErrorCode.INVALID_REQUEST.name(),
            "요청 본문 형식이 올바르지 않습니다."
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto<Void>> exceptionHandler(Exception exception) {
    log.error("Unhandled exception occurred", exception);
    int status = ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value();
    return ResponseEntity.status(status)
        .body(ApiResponseDto.error(status, ErrorCode.INTERNAL_SERVER_ERROR.name(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
  }
}
