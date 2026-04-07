package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

  public UserNotFoundException() {
    super(UserErrorCode.USER_NOT_FOUND);
  }
}

