package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyUserBlockedException extends BusinessException {

  public StudyUserBlockedException() {
    super(StudyErrorCode.STUDY_USER_BLOCKED);
  }
}
