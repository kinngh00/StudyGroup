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
    ExceptionCode exceptionCode = exception.getExceptionCode();
    int status = exceptionCode.getStatus().value();

    return ResponseEntity.status(status)
        .body(ApiResponseDto.error(status, exceptionCode.getCode(), exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto<Void>> validationExceptionHandler(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldError() == null
        ? CommonErrorCode.INVALID_REQUEST.getMessage()
        : exception.getBindingResult().getFieldError().getDefaultMessage();

    return ResponseEntity.status(CommonErrorCode.INVALID_REQUEST.getStatus().value())
        .body(ApiResponseDto.error(
            CommonErrorCode.INVALID_REQUEST.getStatus().value(),
            CommonErrorCode.INVALID_REQUEST.getCode(),
            message
        ));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDto<Void>> httpMessageNotReadableExceptionHandler() {
    return ResponseEntity.status(CommonErrorCode.INVALID_REQUEST.getStatus().value())
        .body(ApiResponseDto.error(
            CommonErrorCode.INVALID_REQUEST.getStatus().value(),
            CommonErrorCode.INVALID_REQUEST.getCode(),
            CommonErrorCode.INVALID_REQUEST.getMessage()
        ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto<Void>> exceptionHandler(Exception exception) {
    log.error("Unhandled exception occurred", exception);
    int status = CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus().value();
    return ResponseEntity.status(status)
        .body(ApiResponseDto.error(
            status,
            CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        ));
  }
}
