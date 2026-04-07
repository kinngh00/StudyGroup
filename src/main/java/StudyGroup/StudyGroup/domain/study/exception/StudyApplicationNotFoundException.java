package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyApplicationNotFoundException extends BusinessException {

  public StudyApplicationNotFoundException() {
    super(StudyErrorCode.STUDY_APPLICATION_NOT_FOUND);
  }
}


