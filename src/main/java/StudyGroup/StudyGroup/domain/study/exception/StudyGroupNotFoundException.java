package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;
import StudyGroup.StudyGroup.global.exception.ErrorCode;

public class StudyGroupNotFoundException extends BusinessException {

  public StudyGroupNotFoundException() {
    super(ErrorCode.STUDY_GROUP_NOT_FOUND);
  }
}
