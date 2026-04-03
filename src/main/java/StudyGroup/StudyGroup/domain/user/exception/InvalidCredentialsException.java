package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;
import StudyGroup.StudyGroup.global.exception.ErrorCode;

public class InvalidCredentialsException extends BusinessException {

  public InvalidCredentialsException() {
    super(ErrorCode.INVALID_CREDENTIALS);
  }
}
