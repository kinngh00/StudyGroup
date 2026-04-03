package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;
import StudyGroup.StudyGroup.global.exception.ErrorCode;

public class InvalidGoogleIdTokenException extends BusinessException {

  public InvalidGoogleIdTokenException() {
    super(ErrorCode.INVALID_GOOGLE_ID_TOKEN);
  }
}
