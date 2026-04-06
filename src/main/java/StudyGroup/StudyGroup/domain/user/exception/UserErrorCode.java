package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionCode {
  DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "Email is already in use."),
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "Invalid email or password."),
  INVALID_GOOGLE_ID_TOKEN("INVALID_GOOGLE_ID_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid Google id token."),
  INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED, "Invalid refresh token."),
  USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "User not found.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
