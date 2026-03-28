package SudyGroup.StudyGroup.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 생성된 이메일입니다.");

  private final HttpStatus status;
  private final String message;

  public String getCode() {
    return this.name();
  }

  public int getStatusValue() {
    return this.status.value();
  }
}
