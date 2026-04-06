package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ExceptionCode {
  DUPLICATE_EMAIL("DUPLICATE_EMAIL", HttpStatus.CONFLICT, "�̹� ��� ���� �̸����Դϴ�."),
  INVALID_CREDENTIALS("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, "�̸��� �Ǵ� ��й�ȣ�� �ùٸ��� �ʽ��ϴ�."),
  INVALID_GOOGLE_ID_TOKEN("INVALID_GOOGLE_ID_TOKEN", HttpStatus.UNAUTHORIZED, "Google id token�� �ùٸ��� �ʽ��ϴ�."),
  INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED, "�������� ��ū�� �ùٸ��� �ʽ��ϴ�."),
  USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "����ڸ� ã�� �� �����ϴ�.");

  private final String code;
  private final HttpStatus status;
  private final String message;
}
