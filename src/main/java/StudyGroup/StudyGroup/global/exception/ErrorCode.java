package StudyGroup.StudyGroup.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
  INVALID_GOOGLE_ID_TOKEN(HttpStatus.UNAUTHORIZED, "Google id token이 올바르지 않습니다."),
  STUDY_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "스터디 그룹을 찾을 수 없습니다."),
  STUDY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "스터디 그룹에 접근할 권한이 없습니다.");

  private final HttpStatus status;
  private final String message;
}
