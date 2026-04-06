package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class InvalidRefreshTokenException extends BusinessException {

  public InvalidRefreshTokenException() {
    super(UserErrorCode.INVALID_REFRESH_TOKEN);
  }
}

