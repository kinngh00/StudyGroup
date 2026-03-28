package SudyGroup.StudyGroup.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
  private final ErrorCode errorCode;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage()); // 부모 생성자에 메시지 전달
    this.errorCode = errorCode;
  }
}
