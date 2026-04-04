package StudyGroup.StudyGroup.global.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

  private final ExceptionCode exceptionCode;

  protected BusinessException(ExceptionCode exceptionCode) {
    super(exceptionCode.getMessage());
    this.exceptionCode = exceptionCode;
  }
}
