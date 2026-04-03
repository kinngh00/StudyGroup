package StudyGroup.StudyGroup.domain.user.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;
import StudyGroup.StudyGroup.global.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {

  public DuplicateEmailException() {
    super(ErrorCode.DUPLICATE_EMAIL);
  }
}
