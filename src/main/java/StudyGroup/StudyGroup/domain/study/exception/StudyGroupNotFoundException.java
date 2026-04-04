package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyGroupNotFoundException extends BusinessException {

  public StudyGroupNotFoundException() {
    super(StudyErrorCode.STUDY_GROUP_NOT_FOUND);
  }
}


