package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionCode {
  DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
  INVALID_GOOGLE_ID_TOKEN("INVALID_GOOGLE_ID_TOKEN", HttpStatus.UNAUTHORIZED, "구글 ID 토큰이 유효하지 않습니다."),
  INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
  USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
