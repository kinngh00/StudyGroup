package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class InvalidCredentialsException extends BusinessException {

  public InvalidCredentialsException() {
    super(UserErrorCode.INVALID_CREDENTIALS);
  }
}


