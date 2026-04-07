package StudyGroup.StudyGroup.domain.study.exception;

import StudyGroup.StudyGroup.global.exception.BusinessException;

public class StudyApplicationDuplicatedException extends BusinessException {

  public StudyApplicationDuplicatedException() {
    super(StudyErrorCode.STUDY_APPLICATION_DUPLICATED);
  }
}


