package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class InvalidGoogleIdTokenException extends BusinessException {

  public InvalidGoogleIdTokenException() {
    super(UserErrorCode.INVALID_GOOGLE_ID_TOKEN);
  }
}


