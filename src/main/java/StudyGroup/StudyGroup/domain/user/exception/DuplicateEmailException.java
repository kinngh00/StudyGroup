package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class DuplicateEmailException extends BusinessException {

  public DuplicateEmailException() {
    super(UserErrorCode.DUPLICATE_EMAIL);
  }
}


